package com.bank.service;

import com.bank.model.Account.InterestStrategyType;

public class InterestStrategyFactory {

    public static InterestStrategy createStrategy(InterestStrategyType type) {
        switch (type) {
            case SIMPLE:
                return new SimpleInterestStrategy();
            case HIGH_YIELD:
                return new HighYieldInterestStrategy();
            default:
                throw new IllegalArgumentException("Unknown interest strategy type: " + type);
        }
    }
}

