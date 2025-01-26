package com.unifiedtradeparser.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@SuperBuilder
@NoArgsConstructor
public class StockTransaction extends Transaction {
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal exchangeQuantity;
    private String exchangeCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal accruedInterest;
}