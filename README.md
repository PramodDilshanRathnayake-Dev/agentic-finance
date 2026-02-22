# Agentic Finance — Investing System v1

Multi-agentic investing system for **Local Market** (v1). FRS v1.0.1 / Architecture v1.0.0.

## Stack

- **Java 21**, **Spring Boot 3.4**
- **H2** (in-memory; replace with PostgreSQL/RDS for production)
- **Event-driven** (Spring Events); gateways are stubs for demo

## Run locally

```bash
mvn spring-boot:run
```

- **API base:** http://localhost:8080
- **H2 console:** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:agenticfinance`)

On startup, a **demo portfolio** is created (initial 1000 + deposit 500) if none exists. Use its `id` from logs for API calls.

## API (pre-prod demo)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/portfolio?initialCapital=1000&currency=USD` | Create portfolio |
| POST | `/api/v1/portfolio/{id}/deposit?amount=50` | Add deposit |
| GET | `/api/v1/portfolio/{id}/capital-state` | Capital state (preserved base, withdrawal cap) |
| GET | `/api/v1/portfolio/{id}/status?queryType=summary` | Verified status (User-facing agent) |
| POST | `/api/v1/trade/{id}/order?symbol=LOCAL-001&side=BUY&quantity=10&orderType=MARKET` | Place order |
| GET | `/api/v1/trade/order/{orderId}` | Order status |
| GET | `/api/v1/withdrawal/{id}/allowed?requestedAmount=25` | Allowed withdrawal amount |
| POST | `/api/v1/withdrawal/{id}?amount=25&currency=USD` | Request withdrawal |
| GET | `/api/v1/analysis/market-data?symbols=LOCAL-001` | Market data |
| POST | `/api/v1/analysis/{id}/risk-metrics?symbols=LOCAL-001,LOCAL-002` | Risk metrics (VaR 90%, CVaR 99%) |
| POST | `/api/v1/analysis/signal?action=BUY&symbol=LOCAL-001&confidence=0.8` | Strategy signal |

## FRS constraints (v1.0.1)

- **Capital preservation:** Total capital ≥ initial capital + deposits (§7.1).
- **Withdrawals:** Capped per `WITHDRAWAL_CAP_PCT`; after withdrawal, capital preservation still holds.
- **VaR/CVaR:** 90% / 99% per §3.
- **Latency:** Trade path target ≤ 1000 ms.

## Project layout

```
src/main/java/com/agenticfinance/
├── config/          # FRS constants, demo data
├── domain/          # Portfolio, Order, Withdrawal, RiskSnapshot, AuditLog
├── repository/      # JPA repositories
├── event/           # Event envelope, types, publisher
├── gateway/         # LocalMarket, Banking (stub implementations)
├── agent/           # System, Analysis, Trade, UserFacing, Observer
├── service/          # PortfolioService
└── web/             # REST controllers
```

## Tests

```bash
mvn test
```

## Pre-prod demonstration

1. Start the app; note the demo portfolio `id` in logs.
2. `GET /api/v1/portfolio/{id}/capital-state` — preserved base, withdrawal cap.
3. `GET /api/v1/portfolio/{id}/status` — verified status (User-facing agent).
4. `POST /api/v1/trade/{id}/order?...` — place a stub order.
5. `POST /api/v1/analysis/{id}/risk-metrics?symbols=LOCAL-001` — risk metrics and event emission.
6. Request withdrawal within cap; verify rejection when it would violate preserved base.

## Docs

- [FRS v1.0.1](docs/frs/FRS-v1.0.1.md)
- [Architecture v1.0.0](docs/architecture/ARCHITECTURE-v1.0.0.md)
- [API Contracts](docs/architecture/API-CONTRACTS-v1.0.0.md)
- [Events and MCP](docs/architecture/EVENTS-AND-MCP-v1.0.0.md)
