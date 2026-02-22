package com.agenticfinance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "position")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal avgPrice;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal marketValue;
}
