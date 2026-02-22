package com.agenticfinance.config;

import com.agenticfinance.repository.PortfolioRepository;
import com.agenticfinance.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Creates a demo portfolio for pre-prod demonstration when no portfolio exists.
 * Disabled in test profile.
 */
@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DemoDataInitializer {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void init() {
        if (portfolioRepository.count() == 0) {
            var p = portfolioService.create(BigDecimal.valueOf(1000), "USD");
            portfolioService.addDeposit(p.getId(), BigDecimal.valueOf(500));
            log.info("Demo portfolio created: id={} (initial 1000 + deposit 500). Preserved base=1500.", p.getId());
        }
    }
}
