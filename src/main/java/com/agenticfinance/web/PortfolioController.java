package com.agenticfinance.web;

import com.agenticfinance.agent.SystemAgent;
import com.agenticfinance.agent.UserFacingAgent;
import com.agenticfinance.service.PortfolioService;
import com.agenticfinance.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final SystemAgent systemAgent;
    private final UserFacingAgent userFacingAgent;

    @PostMapping
    public ResponseEntity<Portfolio> create(
            @RequestParam BigDecimal initialCapital,
            @RequestParam(defaultValue = "USD") String currency) {
        Portfolio p = portfolioService.create(initialCapital, currency);
        return ResponseEntity.ok(p);
    }

    @PostMapping("/{portfolioId}/deposit")
    public ResponseEntity<Void> addDeposit(@PathVariable String portfolioId, @RequestParam BigDecimal amount) {
        portfolioService.addDeposit(portfolioId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{portfolioId}/capital-state")
    public ResponseEntity<SystemAgent.CapitalState> getCapitalState(@PathVariable String portfolioId) {
        return ResponseEntity.ok(systemAgent.getCapitalState(portfolioId));
    }

    @GetMapping("/{portfolioId}/status")
    public ResponseEntity<UserFacingAgent.VerifiedStatus> getVerifiedStatus(
            @PathVariable String portfolioId,
            @RequestParam(defaultValue = "summary") String queryType) {
        return ResponseEntity.ok(userFacingAgent.getVerifiedStatus(portfolioId, queryType));
    }
}
