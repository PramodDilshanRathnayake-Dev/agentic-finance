package com.agenticfinance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Risk metrics per FRS §3: VaR 90%, CVaR 99%.
 */
@Entity
@Table(name = "risk_snapshot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String portfolioId;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal var90;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal cvar99;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal volatility;

    @ElementCollection
    @CollectionTable(name = "risk_snapshot_symbols", joinColumns = @JoinColumn(name = "risk_snapshot_id"))
    @Column(name = "symbol")
    private List<String> symbols;

    @Column(nullable = false)
    private Instant timestamp;
}
