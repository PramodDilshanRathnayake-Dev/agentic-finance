# API Contracts — v1.0.0

**Version:** 1.0.0  
**FRS reference:** [FRS v1.0.1](../frs/FRS-v1.0.1.md)  
**Architecture reference:** [ARCHITECTURE-v1.0.0](ARCHITECTURE-v1.0.0.md)  
**Owner:** Architecture Agent  

---

## 1. LocalMarket API Gateway

Adapter between the Application and Local Market APIs. Protocol: **HTTPS**. Scope: **Local Market only** (v1).

### 1.1 Inbound (Application → Local Market APIs)

| Operation | Method | Path / resource | Request body | Response |
|-----------|--------|-----------------|--------------|----------|
| **Get market data** | GET | `/market/symbols/{symbol}/quote` | — | `{ symbol, price, timestamp, volume }` |
| **Place order** | POST | `/orders` | `{ symbol, side, quantity, orderType, limitPrice? }` | `{ orderId, status, filledQuantity? }` |
| **Cancel order** | DELETE | `/orders/{orderId}` | — | `{ orderId, status }` |
| **Get order status** | GET | `/orders/{orderId}` | — | `{ orderId, status, filledQuantity, avgPrice? }` |

**Symbols:** Local Market symbols (TBD per market provider).  
**Side:** `BUY` | `SELL`.  
**OrderType:** `MARKET` | `LIMIT`.  
**Status:** `PENDING` | `FILLED` | `PARTIALLY_FILLED` | `CANCELLED` | `REJECTED`.

### 1.2 Outbound (Local Market → Application)

| Event / webhook | Description |
|-----------------|-------------|
| **Price update** | Streaming or polling: `{ symbol, price, timestamp }` |
| **Order fill** | Order status change: `{ orderId, status, filledQuantity, avgPrice }` |

*Note:* Exact endpoints depend on the Local Market provider; this contract defines the internal interface the gateway exposes to Analysis and Trade agents.

### 1.3 Error handling

| Code | Meaning |
|------|---------|
| 400 | Invalid request (e.g. invalid symbol, quantity) |
| 401 | Unauthorized (API key / token invalid) |
| 403 | Forbidden (e.g. capital preservation would be violated) |
| 429 | Rate limit exceeded |
| 500 | Provider or gateway error |

---

## 2. Banking API Gateway

Adapter between the Application and Banking APIs. Protocol: **HTTPS**. Used by the **System agent** for deposits and withdrawals.

### 2.1 Inbound (Application → Banking APIs)

| Operation | Method | Path / resource | Request body | Response |
|-----------|--------|-----------------|--------------|----------|
| **Initiate withdrawal** | POST | `/withdrawals` | `{ amount, currency, reference }` | `{ withdrawalId, status }` |
| **Get withdrawal status** | GET | `/withdrawals/{withdrawalId}` | — | `{ withdrawalId, status, completedAt? }` |
| **Get balance** | GET | `/accounts/{accountId}/balance` | — | `{ balance, currency, timestamp }` |

**Status (withdrawal):** `PENDING` | `COMPLETED` | `REJECTED` | `FAILED`.

### 2.2 Outbound (Banking → Application)

| Event | Description |
|-------|-------------|
| **Deposit confirmation** | Deposit credited: `{ depositId, amount, currency, timestamp }` |
| **Withdrawal confirmation** | Withdrawal completed: `{ withdrawalId, status, completedAt }` |

*Note:* Exact endpoints depend on the Banking provider; this contract defines the internal interface the gateway exposes to the System agent.

### 2.3 Invariants (enforced by System agent before call)

- Withdrawal amount ≤ `WITHDRAWAL_CAP_PCT` of current investable capital (per FRS §3), unless overridden by approved user profile.
- After withdrawal, remaining capital ≥ initial capital + deposits (capital preservation, FRS §7.1).

### 2.4 Error handling

| Code | Meaning |
|------|---------|
| 400 | Invalid request (e.g. amount exceeds cap) |
| 401 | Unauthorized |
| 403 | Forbidden (e.g. capital preservation would be violated) |
| 409 | Conflict (e.g. insufficient funds) |
| 500 | Provider or gateway error |

---

## 3. Summary

| Gateway | Primary consumers | Auth |
|---------|-------------------|------|
| **LocalMarket API Gateway** | Analysis agent, Trade agent | API key / OAuth (TBD per provider) |
| **Banking API Gateway** | System agent | API key / OAuth (TBD per provider) |

Secrets stored in **AWS Secrets Manager** per FRS §8.

---

*End of API Contracts v1.0.0*
