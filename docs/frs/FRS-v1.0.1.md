# Functional Requirements Specification — FRS v1.0.1

**Version:** 1.0.1  
**Status:** Draft for User approval  
**Scope:** Local Market only (Crypto out of scope)  
**Single source of truth:** This document and its approved amendments.

**Amendment note:** This version incorporates minor changes agreed by PM Agent and Architecture Agent (v1.0.0 → v1.0.1): VaR 90%, CVaR 99%, latency 1000 ms, and clarified capital preservation wording and example.

---

## 1. Document purpose and scope

This FRS defines the functional and non-functional requirements for the Agentic Finance investing system **version 1**. It is written for User approval; a team reference appendix provides agent-friendly formulations for development and Observer agents.

**In scope for v1:**

- Local Market investing only.
- Application (multi-agent), LocalMarket API gateway, Banking API gateway, Repositories.
- All six system constraints (capital preservation, compounding, ROI behaviour, withdrawals, edge-cases, cost vs ROI).

**Out of scope for v1:**

- Crypto market and Crypto API gateway.

---

## 2. Resolved options (dual options — exact choice and justification)

Every dual option is resolved below with a single choice and short justification. No "and/or" remains open for v1.

| Option category | Resolved choice | Justification |
|-----------------|-----------------|---------------|
| **Cloud provider** | **AWS** | Strong integration with Java/Spring ecosystem, inbuilt dashboards for monitoring, and lower operational familiarity assumed for v1. GCP may be considered in a later version. |
| **Primary external API protocol** | **HTTPS** | Best practice for security and compatibility with Banking and LocalMarket APIs; TLS for confidentiality and integrity. |
| **Agent-to-agent (A2A) / tool protocol** | **MCP** | Model Context Protocol for structured agent-to-agent and tool context; keeps A2A boundaries clear and auditable. |
| **Realtime behaviour** | **Event-driven (Spring)** | Event-driven within the app for scalability and clear separation between market events, analysis, and trade execution. |
| **Market gateway for v1** | **LocalMarket API gateway only** | Scope is Local-only; Crypto gateway deferred. |

---

## 3. Numeric validation targets (for User approval)

All validation thresholds for testing, risk, and cost must be met in v1. Values below are proposed for User approval; replace or confirm before final sign-off.

| Target | Symbol / reference | Proposed value | Unit / note |
|--------|--------------------|----------------|-------------|
| Minimum unit test line coverage | `COVERAGE_MIN` | 80 | % |
| Critical path test coverage | `CRITICAL_PATH_COVERAGE` | 100 | % (all critical flows covered) |
| Value at Risk (VaR) confidence | `VAR_CONFIDENCE` | 90 | % (e.g. 90% VaR) |
| Conditional VaR (CVaR) confidence | `CVAR_CONFIDENCE` | 99 | % |
| Max deployment + maintenance cost vs ROI | `COST_ROI_MAX_RATIO` | 5 | % (deploy + maintenance cost ≤ 5% of annual ROI) |
| Max acceptable latency (trade execution path) | `LATENCY_TRADE_MS` | 1000 | ms |
| Withdrawal cap (per single withdrawal) | `WITHDRAWAL_CAP_PCT` | 10 | % of current investable capital (configurable per user profile) |

**Approval:** User to confirm or amend the above table before FRS v1.0.1 is frozen.

---

## 4. Application agents

### 4.1 User-facing agent (user-trust agent)

- **Purpose:** Communicate with the user about system status; present only verified, non-hallucinated information.
- **Responsibilities:** Surface alerts, portfolio summary, and operational status; filter out known AI hallucinations; support user trust via transparent, accurate messaging.
- **Out of scope for v1:** Crypto-specific UI or logic.

### 4.2 System agent

- **Purpose:** Protect system integrity and handle bank-related operations.
- **Responsibilities:** Enforce capital and withdrawal rules; initiate and confirm bank withdrawals per user-defined schedule; ensure no action reduces capital below the preserved base (initial capital + deposits).

### 4.3 Analysis agent

- **Purpose:** Market data analysis for the Local Market.
- **Responsibilities:** Ingest and analyse LocalMarket data; produce inputs for strategy and risk (e.g. for VaR/CVaR, volatility); no direct trading.

### 4.4 Trade agent

- **Purpose:** Define and execute trading strategies.
- **Responsibilities:** Implement dynamic strategies (continuous or discrete compounding as per strategy model); execute buy/sell in Local Market within capital and risk constraints; align with compounding and ROI constraints.

### 4.5 Observer agent

- **Purpose:** Monitor reasoning and minimise hallucinations across the agentic network.
- **Responsibilities:** Observe self and other agents' reasoning; log for audit and future tuning; feed prompt/MCP and A2A context improvements to reduce hallucinations.

---

## 5. Gateways and repositories

### 5.1 LocalMarket API gateway (in scope for v1)

- Integrates with Local Market data and execution APIs over HTTPS.
- Exposes market data and order execution to the Analysis and Trade agents.
- No Crypto gateway in v1.

### 5.2 Banking API gateway

- Integrates with banking APIs over HTTPS for deposits and withdrawals.
- Used by the System agent for user-defined withdrawals (daily, monthly, annual, or dynamic).
- All withdrawal amounts must leave capital preservation and withdrawal caps satisfied.

### 5.3 Repositories

- Persist portfolio state, orders, risk metrics, and audit logs.
- Support capital and withdrawal invariants (e.g. no write that would violate constraint 1 or 4).

---

## 6. Withdrawals

- **Schedule:** User-defined; may be daily, monthly, annual, or dynamic.
- **Rule:** After every withdrawal, remaining capital must still satisfy **Capital preservation** (see §7.1).
- **Cap:** Single withdrawal must not exceed the FRS-defined cap (e.g. `WITHDRAWAL_CAP_PCT` of current investable capital unless overridden by approved user profile).

---

## 7. Six system constraints (human-friendly — for User approval)

These formulations are for approval by the User. The agent-friendly versions are in **Appendix A** for the development and Observer teams.

### 7.1 Capital preservation

**Human-friendly:**  
At any time, total capital (invested) must never be less than the initial capital plus the sum of all intermediate bank deposits. The system must never reduce the "protected base" (initial capital + deposits).

*Example:* If you started with 100, deposited 50, and later withdraw 25, the system must never let total capital fall below 150. (The protected base remains 150; withdrawals are taken from gains above that base.)

### 7.2 Compounding strategies

**Human-friendly:**  
Investing must follow a compounding-interest approach (growth on growth). The strategy may be continuous or discrete and may change dynamically based on market conditions. Strategies can differ for Local Market (and in future for Crypto, but Crypto is out of scope for v1).

### 7.3 ROI over time

**Human-friendly:**  
Return on investment should increase in the initial phase of the market, then trend toward a stable value relative to total market capital, so that over the long term the system contributes to stable market behaviour rather than unbounded volatility.

### 7.4 User-defined withdrawals

**Human-friendly:**  
The user can define when and how much to withdraw to the bank (e.g. daily, monthly, annually, or dynamic). The system must honour these rules while always keeping remaining capital above the preserved base (see §7.1).

### 7.5 Edge-cases and risk controls

**Human-friendly:**  
The system must be designed to handle edge-cases and risks, including:  
- **VaR and CVaR:** Limits and confidence levels as defined in §3 (VaR 90%, CVaR 99%).  
- **Shark/whale domination:** Behaviour or limits to avoid undue impact from large players.  
- **Hallucinations:** Detection and mitigation (e.g. via Observer agent and prompt/MCP tuning).  
- **Red-team / hacking:** Security and penetration testing to validate resilience.

### 7.6 Cost vs ROI

**Human-friendly:**  
Deployment and maintenance costs must be negligible compared to ROI and system value. A numeric bound is given in §3 (e.g. deploy + maintenance cost ≤ 5% of annual ROI).

---

## 8. Non-functional requirements

- **Security:** HTTPS for all external APIs; MCP for A2A/tools; secrets and credentials managed via cloud provider best practices (e.g. AWS Secrets Manager or equivalent).
- **Scalability:** Event-driven design and stateless agents where possible; repositories and gateways scalable within the chosen cloud (AWS).
- **Observability:** Inbuilt cloud provider dashboards; logging and metrics for trade path, withdrawals, and capital; Observer logs for reasoning and hallucination tuning.
- **Latency:** Trade execution path must meet `LATENCY_TRADE_MS` (1000 ms) per §3.

---

## 9. Approval and changes

- **User approval:** This FRS v1.0.1 is effective only after explicit User approval.
- **Later versions:** Minor changes and optimisations post-production will be documented in subsequent FRS versions (e.g. v1.0.2, v1.1.0) and remain the single source of truth.

---

## Appendix A — Agent-friendly formulations (team reference)

For Architecture, Developer, and Observer agents. Map 1:1 to §7.

| # | Constraint | Agent-friendly formulation |
|---|------------|----------------------------|
| 1 | Capital preservation | \( C(t) \geq C_0 + \sum D_i \) for all \( t \). \( C_0 \) = initial capital; \( D_i \) = deposits. Withdrawals reduce \( C(t) \) but must leave \( C(t) \geq C_0 + \sum D_i \) after each withdrawal. |
| 2 | Compounding strategies | Continuous or discrete compounding; strategy params and type may be market-dependent (Local in v1). |
| 3 | ROI over time | \( \text{ROI}(t) \to \bar{R} \) as \( t \to \infty \) (bounded by total market cap / stable market). |
| 4 | User withdrawals | Withdrawal schedule and caps; after each withdrawal, invariant (1) holds. |
| 5 | Edge-cases | VaR at `VAR_CONFIDENCE` 90%; CVaR at `CVAR_CONFIDENCE` 99%; whale/shark rules; hallucination detection; red-team criteria. |
| 6 | Cost vs ROI | \( \text{DeployCost} + \text{MaintenanceCost} \leq \text{COST\_ROI\_MAX\_RATIO} \times \text{ROI} \). |

---

*End of FRS v1.0.1*
