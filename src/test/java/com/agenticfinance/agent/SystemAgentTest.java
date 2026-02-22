package com.agenticfinance.agent;

import com.agenticfinance.config.FrsConstants;
import com.agenticfinance.domain.Portfolio;
import com.agenticfinance.event.EventPublisher;
import com.agenticfinance.gateway.banking.StubBankingGateway;
import com.agenticfinance.repository.PortfolioRepository;
import com.agenticfinance.repository.WithdrawalRepository;
import com.agenticfinance.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SystemAgentTest {

    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    SystemAgent systemAgent;
    @Autowired
    WithdrawalRepository withdrawalRepository;

    @MockBean
    EventPublisher eventPublisher;

    private String portfolioId;

    @BeforeEach
    void setUp() {
        portfolioRepository.deleteAll();
        withdrawalRepository.deleteAll();
        Portfolio p = portfolioService.create(BigDecimal.valueOf(100), "USD");
        portfolioService.addDeposit(p.getId(), BigDecimal.valueOf(50));
        portfolioId = p.getId();
    }

    @Test
    void getCapitalState() {
        var state = systemAgent.getCapitalState(portfolioId);
        assertThat(state.preservedBase()).isEqualByComparingTo("150");
        assertThat(state.totalCapital()).isEqualByComparingTo("150");
        assertThat(state.availableToInvest()).isEqualByComparingTo("0");
    }

    @Test
    void enforceWithdrawalCap() {
        var allowed = systemAgent.enforceWithdrawalCap(portfolioId, BigDecimal.valueOf(20));
        assertThat(allowed.allowedAmount()).isEqualByComparingTo("0");
    }

    @Test
    void requestWithdrawalRejectedWhenWouldViolatePreservedBase() {
        var result = systemAgent.requestWithdrawal(portfolioId, BigDecimal.valueOf(100), "USD", null);
        assertThat(result.getStatus()).isEqualTo(com.agenticfinance.domain.Withdrawal.WithdrawalStatus.REJECTED);
    }
}
