package com.agenticfinance.web;

import com.agenticfinance.agent.TradeAgent;
import com.agenticfinance.domain.Order;
import com.agenticfinance.gateway.localmarket.OrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeAgent tradeAgent;

    @PostMapping("/{portfolioId}/order")
    public ResponseEntity<OrderResult> placeOrder(
            @PathVariable String portfolioId,
            @RequestParam String symbol,
            @RequestParam Order.OrderSide side,
            @RequestParam BigDecimal quantity,
            @RequestParam(defaultValue = "MARKET") Order.OrderType orderType,
            @RequestParam(required = false) BigDecimal limitPrice) {
        OrderResult result = tradeAgent.placeOrder(portfolioId, symbol, side, quantity, orderType, limitPrice);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResult> getOrderStatus(@PathVariable String orderId) {
        return ResponseEntity.ok(tradeAgent.getOrderStatus(orderId));
    }
}
