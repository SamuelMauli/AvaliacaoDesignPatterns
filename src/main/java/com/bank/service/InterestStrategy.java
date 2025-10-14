package com.bank.service;

import java.math.BigDecimal;

public interface InterestStrategy {
    BigDecimal calculateInterest(BigDecimal balance);
}

