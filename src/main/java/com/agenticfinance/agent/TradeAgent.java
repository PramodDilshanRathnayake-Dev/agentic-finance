package com.agenticfinance.agent;

import com.agenticfinance.config.FrsConstants;
import com.agenticfinance.domain.Order;
import com.agenticfinance.domain.Portfolio;
import com.agenticfinance.event.EventPublisher;
import com.agenticfinance.event.EventTypes;
import com.agenticfinance.gateway.localmarket.LocalMarketGateway;
import com.agenticfinance.gateway.localmarket.OrderResult;
import com.agenticfinance.gateway.localmarket.PlaceOrderRequest;
import com.agenticfinance.repository.OrderRepository;
import com.agenticfinance.repository.PortfolioRepository;
import com.agenticfinance.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Trade agent: place orders within capital constraints. FRS §4.4. Latency target: LATENCY_TRADE_MS.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TradeAgent {

    private static final String SOURCE = "trade-agent";

    private final LocalMarketGateway localMarketGateway;
    private final PortfolioRepository portfolioRepository;
    private final OrderRepository orderRepository;
    private final PortfolioService portfolioService;
    private final SystemAgent systemAgent;
    private final EventPublisher eventPublisher;
    private final FrsConstants frsConstants;

    @Transactional
    public OrderResult placeOrder(String portfolioId, String symbol, Order.OrderSide side, BigDecimal quantity, Order.OrderType orderType, BigDecimal limitPrice) {
        long start = System.currentTimeMillis();
        Portfolio p = portfolioRepository.findById(portfolioId).orElseThrow();
        if (p.getTotalCapital().compareTo(p.getPreservedBase()) < 0) {
            log.warn("Order rejected: capital below preserved base");
            return OrderResult.builder().orderId(null).status(Order.OrderStatus.REJECTED).build();
        }
        PlaceOrderRequest req = PlaceOrderRequest.builder()
                .symbol(symbol)
                .side(side)
                .quantity(quantity)
                .orderType(orderType)
                .limitPrice(limitPrice)
                .build();
        OrderResult result = localMarketGateway.placeOrder(req);
        if (result.getOrderId() != null) {
            Order order = Order.builder()
                    .orderId(result.getOrderId())
                    .portfolioId(portfolioId)
                    .symbol(symbol)
                    .side(side)
                    .quantity(quantity)
                    .orderType(orderType)
                    .limitPrice(limitPrice)
                    .status(result.getStatus())
                    .filledQuantity(result.getFilledQuantity() != null ? result.getFilledQuantity() : BigDecimal.ZERO)
                    .avgPrice(result.getAvgPrice())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            orderRepository.save(order);
            eventPublisher.publish(EventTypes.ORDER_PLACED, SOURCE, Map.of(
                    "orderId", result.getOrderId(),
                    "symbol", symbol,
                    "side", side.name(),
                    "quantity", quantity.doubleValue(),
                    "orderType", orderType.name()
            ));
        }
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed > frsConstants.getLatencyTradeMs())
            log.warn("Trade latency {} ms exceeds FRS target {} ms", elapsed, frsConstants.getLatencyTradeMs());
        return result;
    }

    public OrderResult getOrderStatus(String orderId) {
        return localMarketGateway.getOrderStatus(orderId);
    }

    public void publishTradeStatus(String orderId, String status, String summary) {
        eventPublisher.publish(EventTypes.STATUS_RESPONSE_READY, SOURCE, Map.of(
                "orderId", orderId,
                "status", status,
                "summary", summary != null ? summary : ""
        ));
    }
}
