package com.agenticfinance.service;

import com.agenticfinance.domain.Portfolio;
import com.agenticfinance.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PortfolioServiceTest {

    @Autowired
    PortfolioRepository portfolioRepository;

    @Autowired
    PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        portfolioRepository.deleteAll();
    }

    @Test
    void createPortfolio() {
        Portfolio p = portfolioService.create(BigDecimal.valueOf(100), "USD");
        assertThat(p.getInitialCapital()).isEqualByComparingTo("100");
        assertThat(p.getTotalCapital()).isEqualByComparingTo("100");
        assertThat(p.getPreservedBase()).isEqualByComparingTo("100");
    }

    @Test
    void addDepositIncreasesPreservedBase() {
        Portfolio p = portfolioService.create(BigDecimal.valueOf(100), "USD");
        portfolioService.addDeposit(p.getId(), BigDecimal.valueOf(50));
        Optional<Portfolio> updated = portfolioService.findById(p.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getTotalDeposits()).isEqualByComparingTo("50");
        assertThat(updated.get().getPreservedBase()).isEqualByComparingTo("150");
        assertThat(updated.get().getTotalCapital()).isEqualByComparingTo("150");
    }

    @Test
    void canWithdrawRespectsPreservedBase() {
        Portfolio p = portfolioService.create(BigDecimal.valueOf(100), "USD");
        portfolioService.addDeposit(p.getId(), BigDecimal.valueOf(50));
        assertThat(portfolioService.canWithdraw(p, BigDecimal.valueOf(10))).isTrue();
        assertThat(portfolioService.canWithdraw(p, BigDecimal.valueOf(60))).isFalse();
    }
}
