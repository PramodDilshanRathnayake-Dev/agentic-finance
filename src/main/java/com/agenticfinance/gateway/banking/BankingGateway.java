package com.agenticfinance.gateway.banking;

import java.math.BigDecimal;

/**
 * API contract per API-CONTRACTS-v1.0.0 — Banking API Gateway.
 */
public interface BankingGateway {

    WithdrawalResult initiateWithdrawal(WithdrawalRequest request);

    WithdrawalResult getWithdrawalStatus(String withdrawalId);

    BigDecimal getBalance(String accountId);
}
