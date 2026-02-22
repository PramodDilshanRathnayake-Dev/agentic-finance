package com.agenticfinance.agent;

import com.agenticfinance.domain.Portfolio;
import com.agenticfinance.repository.OrderRepository;
import com.agenticfinance.repository.PortfolioRepository;
import com.agenticfinance.repository.WithdrawalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFacingAgent {

    private final PortfolioRepository portfolioRepository;
    private final OrderRepository orderRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final SystemAgent systemAgent;

    @Transactional(readOnly = true)
    public VerifiedStatus getVerifiedStatus(String portfolioId, String queryType) {
        Map<String, Object> portfolio = new HashMap<>();
        Map<String, Object> alerts = new HashMap<>();
        Map<String, Object> lastTrade = new HashMap<>();

        portfolioRepository.findById(portfolioId).ifPresent(p -> {
            portfolio.put("totalCapital", p.getTotalCapital());
            portfolio.put("preservedBase", p.getPreservedBase());
            portfolio.put("availableToInvest", p.getAvailableToInvest());
            portfolio.put("currency", p.getCurrency());
        });

        SystemAgent.CapitalState capital = systemAgent.getCapitalState(portfolioId);
        portfolio.put("withdrawalCap", capital.getWithdrawalCap());

        orderRepository.findByPortfolioIdOrderByCreatedAtDesc(portfolioId, org.springframework.data.domain.PageRequest.of(0, 1))
                .stream().findFirst().ifPresent(o -> {
                    lastTrade.put("orderId", o.getOrderId());
                    lastTrade.put("symbol", o.getSymbol());
                    lastTrade.put("status", o.getStatus().name());
                });

        return new VerifiedStatus(portfolio, alerts, lastTrade, true);
    }

    public String respondToUser(String queryId, String response) {
        return response;
    }

    public record VerifiedStatus(Map<String, Object> portfolio, Map<String, Object> alerts, Map<String, Object> lastTrade, boolean verified) {}
}
