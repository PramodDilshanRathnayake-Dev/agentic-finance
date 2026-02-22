package com.agenticfinance.gateway.banking;

import com.agenticfinance.domain.Withdrawal;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalResult {

    private String withdrawalId;
    private Withdrawal.WithdrawalStatus status;
    private Instant completedAt;
}
