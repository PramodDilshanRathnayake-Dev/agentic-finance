package com.agenticfinance.gateway.banking;

import com.agenticfinance.domain.Withdrawal;
import com.agenticfinance.event.EventPublisher;
import com.agenticfinance.event.EventTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stub implementation for dev/demo. Replace with real HTTP client for production.
 */
@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class StubBankingGateway implements BankingGateway {

    private final EventPublisher eventPublisher;
    private final Map<String, WithdrawalResult> withdrawals = new ConcurrentHashMap<>();

    @Override
    public WithdrawalResult initiateWithdrawal(WithdrawalRequest request) {
        String withdrawalId = "wth-" + UUID.randomUUID();
        WithdrawalResult result = WithdrawalResult.builder()
                .withdrawalId(withdrawalId)
                .status(Withdrawal.WithdrawalStatus.PENDING)
                .completedAt(null)
                .build();
        withdrawals.put(withdrawalId, result);
        eventPublisher.publish(EventTypes.WITHDRAWAL_REQUESTED, "banking-gateway", Map.of(
                "withdrawalId", withdrawalId,
                "amount", request.getAmount().doubleValue(),
                "currency", request.getCurrency()
        ));
        log.info("Stub withdrawal requested: {} {}", request.getAmount(), request.getCurrency());
        return result;
    }

    @Override
    public WithdrawalResult getWithdrawalStatus(String withdrawalId) {
        WithdrawalResult r = withdrawals.get(withdrawalId);
        if (r != null)
            return r;
        return WithdrawalResult.builder()
                .withdrawalId(withdrawalId)
                .status(Withdrawal.WithdrawalStatus.FAILED)
                .build();
    }

    @Override
    public BigDecimal getBalance(String accountId) {
        return BigDecimal.valueOf(10000); // stub
    }
}
