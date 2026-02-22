package com.agenticfinance.event;

/**
 * Event types per EVENTS-AND-MCP-v1.0.0.
 */
public final class EventTypes {

    public static final String MARKET_DATA_RECEIVED = "MarketDataReceived";
    public static final String RISK_METRICS_READY = "RiskMetricsReady";
    public static final String STRATEGY_SIGNAL = "StrategySignal";
    public static final String CAPITAL_CONSTRAINT_UPDATE = "CapitalConstraintUpdate";
    public static final String ORDER_PLACED = "OrderPlaced";
    public static final String ORDER_FILLED = "OrderFilled";
    public static final String WITHDRAWAL_REQUESTED = "WithdrawalRequested";
    public static final String WITHDRAWAL_COMPLETED = "WithdrawalCompleted";
    public static final String USER_QUERY_RECEIVED = "UserQueryReceived";
    public static final String STATUS_RESPONSE_READY = "StatusResponseReady";
    public static final String AGENT_REASONING = "AgentReasoning";
    public static final String HALLUCINATION_ALERT = "HallucinationAlert";

    private EventTypes() {}
}
