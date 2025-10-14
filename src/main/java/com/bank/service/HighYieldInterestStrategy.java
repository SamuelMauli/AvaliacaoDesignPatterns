package com.bank.service;

import java.math.BigDecimal;

public class HighYieldInterestStrategy implements InterestStrategy {
    private static final BigDecimal HIGH_YIELD_RATE = new BigDecimal("0.05"); // 5%

    @Override
    public BigDecimal calculateInterest(BigDecimal balance) {
        return balance.multiply(HIGH_YIELD_RATE);
    }
}

