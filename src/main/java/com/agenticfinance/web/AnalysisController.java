package com.agenticfinance.web;

import com.agenticfinance.agent.AnalysisAgent;
import com.agenticfinance.gateway.localmarket.MarketQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisAgent analysisAgent;

    @GetMapping("/market-data")
    public ResponseEntity<List<MarketQuote>> getMarketData(@RequestParam List<String> symbols) {
        return ResponseEntity.ok(analysisAgent.getMarketData(symbols));
    }

    @PostMapping("/{portfolioId}/risk-metrics")
    public ResponseEntity<AnalysisAgent.RiskMetrics> computeRiskMetrics(
            @PathVariable String portfolioId,
            @RequestParam List<String> symbols) {
        return ResponseEntity.ok(analysisAgent.computeAndPublishRiskMetrics(portfolioId, symbols));
    }

    @PostMapping("/signal")
    public ResponseEntity<Void> publishStrategySignal(
            @RequestParam String action,
            @RequestParam String symbol,
            @RequestParam(defaultValue = "0.8") double confidence,
            @RequestParam(required = false) String rationale) {
        analysisAgent.publishStrategySignal(action, symbol, confidence, rationale);
        return ResponseEntity.ok().build();
    }
}
