package com.bank.service;

import java.math.BigDecimal;

public class SimpleInterestStrategy implements InterestStrategy {
    private static final BigDecimal SIMPLE_RATE = new BigDecimal("0.01"); // 1%

    @Override
    public BigDecimal calculateInterest(BigDecimal balance) {
        return balance.multiply(SIMPLE_RATE);
    }
}

