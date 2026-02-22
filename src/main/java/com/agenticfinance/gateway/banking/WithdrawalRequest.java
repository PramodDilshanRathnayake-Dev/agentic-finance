package com.agenticfinance.gateway.banking;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalRequest {

    private BigDecimal amount;
    private String currency;
    private String reference;
}
