package com.agenticfinance.agent;

import com.agenticfinance.config.FrsConstants;
import com.agenticfinance.domain.Portfolio;
import com.agenticfinance.domain.Withdrawal;
import com.agenticfinance.event.EventPublisher;
import com.agenticfinance.event.EventTypes;
import com.agenticfinance.gateway.banking.BankingGateway;
import com.agenticfinance.gateway.banking.WithdrawalRequest;
import com.agenticfinance.gateway.banking.WithdrawalResult;
import com.agenticfinance.repository.PortfolioRepository;
import com.agenticfinance.repository.WithdrawalRepository;
import com.agenticfinance.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * System agent: capital preservation, withdrawal enforcement, Banking gateway.
 * FRS §4.2.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAgent {

    private static final String SOURCE = "system-agent";

    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;
    private final WithdrawalRepository withdrawalRepository;
    private final BankingGateway bankingGateway;
    private final EventPublisher eventPublisher;
    private final FrsConstants frsConstants;

    @Transactional(readOnly = true)
    public CapitalState getCapitalState(String portfolioId) {
        Portfolio p = portfolioRepository.findById(portfolioId).orElseThrow();
        BigDecimal cap = portfolioService.withdrawalCapAmount(p);
        return CapitalState.builder()
                .preservedBase(p.getPreservedBase())
                .totalCapital(p.getTotalCapital())
                .availableToInvest(p.getAvailableToInvest())
                .withdrawalCap(cap)
                .build();
    }

    /** Emit capital constraint update for Trade agent. */
    public void emitCapitalConstraint(String portfolioId) {
        CapitalState state = getCapitalState(portfolioId);
        eventPublisher.publish(EventTypes.CAPITAL_CONSTRAINT_UPDATE, SOURCE, Map.of(
                "preservedBase", state.getPreservedBase().doubleValue(),
                "availableToInvest", state.getAvailableToInvest().doubleValue(),
                "withdrawalCap", state.getWithdrawalCap().doubleValue()
        ));
    }

    /** Enforce withdrawal cap; return allowed amount. */
    public AllowedWithdrawal enforceWithdrawalCap(String portfolioId, BigDecimal requestedAmount) {
        Portfolio p = portfolioRepository.findById(portfolioId).orElseThrow();
        BigDecimal cap = portfolioService.withdrawalCapAmount(p);
        if (requestedAmount.compareTo(cap) <= 0 && portfolioService.canWithdraw(p, requestedAmount))
            return new AllowedWithdrawal(requestedAmount, "OK");
        BigDecimal allowed = cap.min(requestedAmount).max(BigDecimal.ZERO);
        return new AllowedWithdrawal(allowed, "Capped to " + cap);
    }

    @Transactional
    public WithdrawalResult requestWithdrawal(String portfolioId, BigDecimal amount, String currency, String reference) {
        Portfolio p = portfolioRepository.findById(portfolioId).orElseThrow();
        if (!portfolioService.canWithdraw(p, amount)) {
            log.warn("Withdrawal rejected: capital preservation or cap violated");
            return WithdrawalResult.builder()
                    .withdrawalId(null)
                    .status(Withdrawal.WithdrawalStatus.REJECTED)
                    .build();
        }
        WithdrawalRequest req = WithdrawalRequest.builder()
                .amount(amount)
                .currency(currency != null ? currency : p.getCurrency())
                .reference(reference)
                .build();
        WithdrawalResult result = bankingGateway.initiateWithdrawal(req);
        if (result.getWithdrawalId() != null) {
            Withdrawal w = Withdrawal.builder()
                    .withdrawalId(result.getWithdrawalId())
                    .portfolioId(portfolioId)
                    .amount(amount)
                    .currency(req.getCurrency())
                    .status(result.getStatus())
                    .reference(reference)
                    .requestedAt(Instant.now())
                    .build();
            withdrawalRepository.save(w);
            p.setTotalCapital(p.getTotalCapital().subtract(amount));
            p.setCashBalance(p.getCashBalance().subtract(amount));
            p.setUpdatedAt(Instant.now());
            portfolioRepository.save(p);
        }
        return result;
    }

    public record CapitalState(BigDecimal preservedBase, BigDecimal totalCapital, BigDecimal availableToInvest, BigDecimal withdrawalCap) {
        public static CapitalStateBuilder builder() { return new CapitalStateBuilder(); }
        public static class CapitalStateBuilder {
            private BigDecimal preservedBase;
            private BigDecimal totalCapital;
            private BigDecimal availableToInvest;
            private BigDecimal withdrawalCap;
            public CapitalStateBuilder preservedBase(BigDecimal v) { this.preservedBase = v; return this; }
            public CapitalStateBuilder totalCapital(BigDecimal v) { this.totalCapital = v; return this; }
            public CapitalStateBuilder availableToInvest(BigDecimal v) { this.availableToInvest = v; return this; }
            public CapitalStateBuilder withdrawalCap(BigDecimal v) { this.withdrawalCap = v; return this; }
            public CapitalState build() { return new CapitalState(preservedBase, totalCapital, availableToInvest, withdrawalCap); }
        }
    }

    public record AllowedWithdrawal(BigDecimal allowedAmount, String reason) {}
}
