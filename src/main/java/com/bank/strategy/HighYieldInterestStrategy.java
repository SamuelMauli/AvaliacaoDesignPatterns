package com.bank.strategy;

public class HighYieldInterestStrategy implements InterestCalculationStrategy {
    @Override
    public double calculateInterest(double balance, double interestRate) {
        // Example: High yield could be a slightly higher rate or a bonus
        return balance * (interestRate + 0.01); // 1% bonus interest
    }
}
