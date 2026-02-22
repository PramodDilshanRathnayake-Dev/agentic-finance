package com.agenticfinance.gateway.localmarket;

import com.agenticfinance.domain.Order;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResult {

    private String orderId;
    private Order.OrderStatus status;
    private BigDecimal filledQuantity;
    private BigDecimal avgPrice;
}
