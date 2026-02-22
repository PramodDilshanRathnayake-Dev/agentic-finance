package com.agenticfinance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    private String orderId;

    @Column(nullable = false)
    private String portfolioId;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Column(precision = 19, scale = 4)
    private BigDecimal limitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(precision = 19, scale = 6)
    private BigDecimal filledQuantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal avgPrice;

    private Instant createdAt;
    private Instant updatedAt;

    public enum OrderSide { BUY, SELL }
    public enum OrderType { MARKET, LIMIT }
    public enum OrderStatus { PENDING, FILLED, PARTIALLY_FILLED, CANCELLED, REJECTED }
}
