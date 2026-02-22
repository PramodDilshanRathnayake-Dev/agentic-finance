package com.agenticfinance.gateway.localmarket;

import com.agenticfinance.domain.Order;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderRequest {

    private String symbol;
    private Order.OrderSide side;
    private BigDecimal quantity;
    private Order.OrderType orderType;
    private BigDecimal limitPrice;
}
