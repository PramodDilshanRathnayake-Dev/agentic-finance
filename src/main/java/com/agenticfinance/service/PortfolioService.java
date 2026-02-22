package com.agenticfinance.service;

import com.agenticfinance.config.FrsConstants;
import com.agenticfinance.domain.Portfolio;
import com.agenticfinance.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final FrsConstants frsConstants;

    @Transactional(readOnly = true)
    public Optional<Portfolio> findById(String id) {
        return portfolioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Portfolio> findByIdWithPositions(String id) {
        return portfolioRepository.findByIdWithPositions(id);
    }

    @Transactional
    public Portfolio create(BigDecimal initialCapital, String currency) {
        Portfolio p = Portfolio.builder()
                .initialCapital(initialCapital)
                .totalDeposits(BigDecimal.ZERO)
                .totalCapital(initialCapital)
                .cashBalance(initialCapital)
                .currency(currency)
                .updatedAt(Instant.now())
                .build();
        return portfolioRepository.save(p);
    }

    @Transactional
    public void addDeposit(String portfolioId, BigDecimal amount) {
        Portfolio p = portfolioRepository.findById(portfolioId).orElseThrow();
        p.setTotalDeposits(p.getTotalDeposits().add(amount));
        p.setTotalCapital(p.getTotalCapital().add(amount));
        p.setCashBalance(p.getCashBalance().add(amount));
        p.setUpdatedAt(Instant.now());
        portfolioRepository.save(p);
    }

    public BigDecimal withdrawalCapAmount(Portfolio p) {
        BigDecimal investable = p.getTotalCapital().subtract(p.getPreservedBase()).max(BigDecimal.ZERO);
        return investable.multiply(BigDecimal.valueOf(frsConstants.getWithdrawalCapPct()))
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    public boolean canWithdraw(Portfolio p, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal after = p.getTotalCapital().subtract(amount);
        return after.compareTo(p.getPreservedBase()) >= 0 && amount.compareTo(withdrawalCapAmount(p)) <= 0;
    }
}
