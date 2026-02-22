package com.agenticfinance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Portfolio aggregate: capital preservation base = initialCapital + sum(deposits).
 * FRS §7.1: C(t) >= C0 + sum(Di).
 */
@Entity
@Table(name = "portfolio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal initialCapital;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDeposits;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalCapital;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashBalance;

    @Column(nullable = false)
    private String currency;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Position> positions = new ArrayList<>();

    @Version
    private long version;

    @Column(nullable = false)
    private Instant updatedAt;

    /** Preserved base: initial capital + deposits. Must never exceed totalCapital. */
    public BigDecimal getPreservedBase() {
        return initialCapital.add(totalDeposits);
    }

    /** Available to invest = totalCapital - preservedBase (only gains). */
    public BigDecimal getAvailableToInvest() {
        return totalCapital.subtract(getPreservedBase()).max(BigDecimal.ZERO);
    }
}
