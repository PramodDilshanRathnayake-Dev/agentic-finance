# Event Schema and MCP Tool Definitions — v1.0.0

**Version:** 1.0.0  
**FRS reference:** [FRS v1.0.1](../frs/FRS-v1.0.1.md)  
**Architecture reference:** [ARCHITECTURE-v1.0.0](ARCHITECTURE-v1.0.0.md)  
**Owner:** Architecture Agent  

---

## 1. Event schema (event-driven flows)

All events use a common envelope. Schema version: `1.0`.

### 1.1 Event envelope

```json
{
  "eventId": "uuid",
  "eventType": "string",
  "timestamp": "ISO8601",
  "source": "string",
  "payload": { }
}
```

- **eventId:** Unique identifier (UUID v4).
- **eventType:** One of the types below.
- **timestamp:** UTC ISO8601.
- **source:** Producer agent or component (e.g. `analysis-agent`, `trade-agent`).
- **payload:** Event-specific data.

---

### 1.2 Event types

| Event type | Source | Payload | Consumers |
|------------|--------|---------|-----------|
| `MarketDataReceived` | LocalMarket Gateway | `{ symbol, price, volume, timestamp }` | Analysis agent |
| `RiskMetricsReady` | Analysis agent | `{ var90, cvar99, volatility, symbols[] }` | Trade agent |
| `StrategySignal` | Analysis agent | `{ action: BUY\|SELL\|HOLD, symbol, confidence, rationale }` | Trade agent |
| `CapitalConstraintUpdate` | System agent | `{ preservedBase, availableToInvest, withdrawalCap }` | Trade agent |
| `OrderPlaced` | Trade agent | `{ orderId, symbol, side, quantity, orderType }` | Repositories, Observer |
| `OrderFilled` | LocalMarket Gateway | `{ orderId, filledQuantity, avgPrice, status }` | Trade agent, Repositories |
| `WithdrawalRequested` | System agent | `{ withdrawalId, amount, currency }` | Banking Gateway |
| `WithdrawalCompleted` | Banking Gateway | `{ withdrawalId, status }` | System agent, Repositories |
| `UserQueryReceived` | User-facing agent | `{ queryId, query }` | User-facing (routing) |
| `StatusResponseReady` | User-facing agent | `{ queryId, response, verified }` | User |
| `AgentReasoning` | Any agent | `{ agentId, trace, promptRef }` | Observer agent |
| `HallucinationAlert` | Observer agent | `{ agentId, alertType, suggestion }` | Architecture / tuning |

---

### 1.3 Example events

**MarketDataReceived:**

```json
{
  "eventId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "eventType": "MarketDataReceived",
  "timestamp": "2025-02-22T10:00:00.000Z",
  "source": "localmarket-gateway",
  "payload": {
    "symbol": "LOCAL-001",
    "price": 42.50,
    "volume": 1000,
    "timestamp": "2025-02-22T10:00:00.000Z"
  }
}
```

**RiskMetricsReady:**

```json
{
  "eventId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "eventType": "RiskMetricsReady",
  "timestamp": "2025-02-22T10:00:05.000Z",
  "source": "analysis-agent",
  "payload": {
    "var90": -0.02,
    "cvar99": -0.05,
    "volatility": 0.15,
    "symbols": ["LOCAL-001", "LOCAL-002"]
  }
}
```

**CapitalConstraintUpdate:**

```json
{
  "eventId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
  "eventType": "CapitalConstraintUpdate",
  "timestamp": "2025-02-22T10:00:00.000Z",
  "source": "system-agent",
  "payload": {
    "preservedBase": 150.00,
    "availableToInvest": 75.00,
    "withdrawalCap": 15.00
  }
}
```

---

## 2. MCP tool definitions (agent-to-agent / tools)

MCP (Model Context Protocol) is used for A2A and tool exposure. Tools below are exposed by agents for use by other agents.

### 2.1 System agent — MCP tools

| Tool name | Description | Input | Output |
|-----------|-------------|-------|--------|
| `get_capital_state` | Returns current capital and preservation constraints | `{}` | `{ preservedBase, totalCapital, availableToInvest, withdrawalCap }` |
| `enforce_withdrawal_cap` | Validates withdrawal against cap; returns allowed amount | `{ requestedAmount }` | `{ allowedAmount, reason }` |
| `emit_capital_constraint` | Publishes capital constraint update to Trade agent | `{ preservedBase, availableToInvest, withdrawalCap }` | `{ success }` |

### 2.2 Analysis agent — MCP tools

| Tool name | Description | Input | Output |
|-----------|-------------|-------|--------|
| `publish_risk_metrics` | Publishes VaR 90%, CVaR 99%, volatility | `{ var90, cvar99, volatility, symbols }` | `{ success }` |
| `publish_strategy_signal` | Publishes BUY/SELL/HOLD signal to Trade agent | `{ action, symbol, confidence, rationale }` | `{ success }` |
| `get_market_data` | Fetches latest market data for symbol(s) | `{ symbols[] }` | `{ quotes[] }` |

### 2.3 Trade agent — MCP tools

| Tool name | Description | Input | Output |
|-----------|-------------|-------|--------|
| `place_order` | Places order via LocalMarket Gateway | `{ symbol, side, quantity, orderType, limitPrice? }` | `{ orderId, status }` |
| `get_order_status` | Returns order status | `{ orderId }` | `{ orderId, status, filledQuantity, avgPrice }` |
| `publish_trade_status` | Publishes trade status to User-facing agent | `{ orderId, status, summary }` | `{ success }` |

### 2.4 User-facing agent — MCP tools

| Tool name | Description | Input | Output |
|-----------|-------------|-------|--------|
| `get_verified_status` | Aggregates verified status from System/Analysis/Trade | `{ queryType }` | `{ portfolio, alerts, lastTrade, verified }` |
| `respond_to_user` | Sends filtered, non-hallucinated response to User | `{ queryId, response }` | `{ success }` |

### 2.5 Observer agent — MCP tools (internal)

| Tool name | Description | Input | Output |
|-----------|-------------|-------|--------|
| `log_reasoning` | Logs agent reasoning to audit | `{ agentId, trace, promptRef }` | `{ success }` |
| `emit_hallucination_alert` | Emits hallucination alert for tuning | `{ agentId, alertType, suggestion }` | `{ success }` |

---

## 3. Event bus implementation

- **Technology:** Spring Cloud Stream or Spring Events (in-process) for v1.
- **Future:** Consider AWS EventBridge or SNS/SQS for distributed, multi-container deployment.
- **Ordering:** Trade execution path events should be processed in order; idempotency keys (`eventId`) for at-least-once delivery.

---

## 4. Summary

| Layer | Technology |
|-------|------------|
| **Event schema** | JSON envelope; types defined above |
| **Event bus** | Spring Events / Spring Cloud Stream (v1) |
| **MCP** | Model Context Protocol for A2A tools |
| **Idempotency** | `eventId` for deduplication |

---

*End of Events and MCP v1.0.0*
