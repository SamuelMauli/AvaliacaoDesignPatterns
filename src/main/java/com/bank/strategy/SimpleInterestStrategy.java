package com.bank.strategy;

public class SimpleInterestStrategy implements InterestCalculationStrategy {
    @Override
    public double calculateInterest(double balance, double interestRate) {
        return balance * interestRate;
    }
}
