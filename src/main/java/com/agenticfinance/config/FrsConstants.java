package com.agenticfinance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * FRS v1.0.1 numeric validation targets.
 */
@Configuration
@ConfigurationProperties(prefix = "app.frs")
public class FrsConstants {

    private int varConfidence = 90;
    private int cvarConfidence = 99;
    private int latencyTradeMs = 1000;
    private int withdrawalCapPct = 10;
    private int coverageMin = 80;
    private int costRoiMaxRatio = 5;

    public int getVarConfidence() { return varConfidence; }
    public void setVarConfidence(int varConfidence) { this.varConfidence = varConfidence; }

    public int getCvarConfidence() { return cvarConfidence; }
    public void setCvarConfidence(int cvarConfidence) { this.cvarConfidence = cvarConfidence; }

    public int getLatencyTradeMs() { return latencyTradeMs; }
    public void setLatencyTradeMs(int latencyTradeMs) { this.latencyTradeMs = latencyTradeMs; }

    public int getWithdrawalCapPct() { return withdrawalCapPct; }
    public void setWithdrawalCapPct(int withdrawalCapPct) { this.withdrawalCapPct = withdrawalCapPct; }

    public int getCoverageMin() { return coverageMin; }
    public void setCoverageMin(int coverageMin) { this.coverageMin = coverageMin; }

    public int getCostRoiMaxRatio() { return costRoiMaxRatio; }
    public void setCostRoiMaxRatio(int costRoiMaxRatio) { this.costRoiMaxRatio = costRoiMaxRatio; }
}
