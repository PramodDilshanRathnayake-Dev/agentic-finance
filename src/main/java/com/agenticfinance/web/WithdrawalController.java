package com.agenticfinance.web;

import com.agenticfinance.agent.SystemAgent;
import com.agenticfinance.gateway.banking.WithdrawalResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/withdrawal")
@RequiredArgsConstructor
public class WithdrawalController {

    private final SystemAgent systemAgent;

    @GetMapping("/{portfolioId}/allowed")
    public ResponseEntity<SystemAgent.AllowedWithdrawal> getAllowedWithdrawal(
            @PathVariable String portfolioId,
            @RequestParam BigDecimal requestedAmount) {
        return ResponseEntity.ok(systemAgent.enforceWithdrawalCap(portfolioId, requestedAmount));
    }

    @PostMapping("/{portfolioId}")
    public ResponseEntity<WithdrawalResult> requestWithdrawal(
            @PathVariable String portfolioId,
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "USD") String currency,
            @RequestParam(required = false) String reference) {
        WithdrawalResult result = systemAgent.requestWithdrawal(portfolioId, amount, currency, reference);
        return ResponseEntity.ok(result);
    }
}
