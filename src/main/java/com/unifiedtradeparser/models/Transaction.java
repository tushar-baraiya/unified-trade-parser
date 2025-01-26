package com.unifiedtradeparser.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Transaction {
    private LocalDate runDate;
    private String account;
    private String action;
    private BigDecimal amount;
    private String currency;
    private BigDecimal commission;
    private BigDecimal fees;
    private LocalDate settlementDate;
    private String type;
    private String description;
}