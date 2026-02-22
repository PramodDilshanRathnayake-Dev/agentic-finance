package com.agenticfinance.gateway.localmarket;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketQuote {

    private String symbol;
    private BigDecimal price;
    private BigDecimal volume;
    private Instant timestamp;
}
