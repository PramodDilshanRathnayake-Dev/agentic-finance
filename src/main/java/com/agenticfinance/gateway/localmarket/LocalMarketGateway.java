package com.agenticfinance.gateway.localmarket;

import java.util.List;

/**
 * API contract per API-CONTRACTS-v1.0.0 — LocalMarket API Gateway.
 */
public interface LocalMarketGateway {

    MarketQuote getQuote(String symbol);

    List<MarketQuote> getQuotes(List<String> symbols);

    OrderResult placeOrder(PlaceOrderRequest request);

    OrderResult getOrderStatus(String orderId);

    OrderResult cancelOrder(String orderId);
}
