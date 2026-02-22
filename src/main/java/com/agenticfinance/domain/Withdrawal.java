package com.agenticfinance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "withdrawal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Withdrawal {

    @Id
    private String withdrawalId;

    @Column(nullable = false)
    private String portfolioId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status;

    private String reference;
    private Instant requestedAt;
    private Instant completedAt;

    public enum WithdrawalStatus { PENDING, COMPLETED, REJECTED, FAILED }
}
