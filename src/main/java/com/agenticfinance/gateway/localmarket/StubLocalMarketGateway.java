package com.agenticfinance.gateway.localmarket;

import com.agenticfinance.domain.Order;
import com.agenticfinance.event.EventPublisher;
import com.agenticfinance.event.EventTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stub implementation for dev/demo. Replace with real HTTP client for production.
 */
@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class StubLocalMarketGateway implements LocalMarketGateway {

    private final EventPublisher eventPublisher;
    private final Map<String, OrderResult> orders = new ConcurrentHashMap<>();

    @Override
    public MarketQuote getQuote(String symbol) {
        BigDecimal price = BigDecimal.valueOf(40 + Math.random() * 20).setScale(2, java.math.RoundingMode.HALF_UP);
        MarketQuote q = MarketQuote.builder()
                .symbol(symbol)
                .price(price)
                .volume(BigDecimal.valueOf(1000))
                .timestamp(Instant.now())
                .build();
        eventPublisher.publish(EventTypes.MARKET_DATA_RECEIVED, "localmarket-gateway", Map.of(
                "symbol", symbol,
                "price", price.doubleValue(),
                "volume", 1000,
                "timestamp", Instant.now().toString()
        ));
        return q;
    }

    @Override
    public List<MarketQuote> getQuotes(List<String> symbols) {
        return symbols.stream().map(this::getQuote).toList();
    }

    @Override
    public OrderResult placeOrder(PlaceOrderRequest request) {
        String orderId = "ord-" + UUID.randomUUID();
        OrderResult result = OrderResult.builder()
                .orderId(orderId)
                .status(Order.OrderStatus.PENDING)
                .filledQuantity(BigDecimal.ZERO)
                .avgPrice(null)
                .build();
        orders.put(orderId, result);
        log.info("Stub order placed: {} {} {} @ {}", request.getSide(), request.getQuantity(), request.getSymbol(), request.getOrderType());
        return result;
    }

    @Override
    public OrderResult getOrderStatus(String orderId) {
        return orders.getOrDefault(orderId, OrderResult.builder()
                .orderId(orderId)
                .status(Order.OrderStatus.REJECTED)
                .build());
    }

    @Override
    public OrderResult cancelOrder(String orderId) {
        OrderResult r = orders.get(orderId);
        if (r != null) {
            orders.put(orderId, OrderResult.builder()
                    .orderId(orderId)
                    .status(Order.OrderStatus.CANCELLED)
                    .filledQuantity(r.getFilledQuantity())
                    .avgPrice(r.getAvgPrice())
                    .build());
        }
        return getOrderStatus(orderId);
    }
}
