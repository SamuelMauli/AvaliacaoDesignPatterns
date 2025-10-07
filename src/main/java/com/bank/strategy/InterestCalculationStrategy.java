package com.bank.strategy;

public interface InterestCalculationStrategy {
    double calculateInterest(double balance, double interestRate);
}
