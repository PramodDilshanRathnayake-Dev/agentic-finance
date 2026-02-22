package com.agenticfinance.agent;

import com.agenticfinance.config.FrsConstants;
import com.agenticfinance.domain.RiskSnapshot;
import com.agenticfinance.event.EventPublisher;
import com.agenticfinance.event.EventTypes;
import com.agenticfinance.gateway.localmarket.LocalMarketGateway;
import com.agenticfinance.gateway.localmarket.MarketQuote;
import com.agenticfinance.repository.RiskSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Analysis agent: market data, VaR 90%, CVaR 99%, strategy signals.
 * FRS §4.3. No direct trading.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisAgent {

    private static final String SOURCE = "analysis-agent";

    private final LocalMarketGateway localMarketGateway;
    private final RiskSnapshotRepository riskSnapshotRepository;
    private final EventPublisher eventPublisher;
    private final FrsConstants frsConstants;

    public List<MarketQuote> getMarketData(List<String> symbols) {
        return localMarketGateway.getQuotes(symbols);
    }

    /**
     * Compute stub risk metrics (VaR 90%, CVaR 99%). Production would use historical/parametric model.
     */
    @Transactional
    public RiskMetrics computeAndPublishRiskMetrics(String portfolioId, List<String> symbols) {
        List<MarketQuote> quotes = localMarketGateway.getQuotes(symbols);
        BigDecimal var90 = BigDecimal.valueOf(-0.02);
        BigDecimal cvar99 = BigDecimal.valueOf(-0.05);
        BigDecimal volatility = BigDecimal.valueOf(0.15);
        List<String> syms = quotes.stream().map(MarketQuote::getSymbol).toList();

        eventPublisher.publish(EventTypes.RISK_METRICS_READY, SOURCE, Map.of(
                "var90", var90.doubleValue(),
                "cvar99", cvar99.doubleValue(),
                "volatility", volatility.doubleValue(),
                "symbols", syms
        ));

        RiskSnapshot snapshot = RiskSnapshot.builder()
                .portfolioId(portfolioId)
                .var90(var90)
                .cvar99(cvar99)
                .volatility(volatility)
                .symbols(syms)
                .timestamp(Instant.now())
                .build();
        riskSnapshotRepository.save(snapshot);

        return new RiskMetrics(var90, cvar99, volatility, syms);
    }

    /** Emit strategy signal (BUY/SELL/HOLD) to Trade agent. */
    public void publishStrategySignal(String action, String symbol, double confidence, String rationale) {
        eventPublisher.publish(EventTypes.STRATEGY_SIGNAL, SOURCE, Map.of(
                "action", action,
                "symbol", symbol,
                "confidence", confidence,
                "rationale", rationale != null ? rationale : ""
        ));
    }

    public record RiskMetrics(BigDecimal var90, BigDecimal cvar99, BigDecimal volatility, List<String> symbols) {}
}
