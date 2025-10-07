package com.bank.account;

import com.bank.strategy.InterestCalculationStrategy;
import com.bank.strategy.SimpleInterestStrategy;

public class SavingsAccount extends Account implements Withdrawable, InterestBearing {
    private InterestCalculationStrategy interestStrategy;
    private double interestRate;

    public SavingsAccount(String customerName, double initialBalance, double interestRate) {
        super(customerName, initialBalance);
        this.interestRate = interestRate;
        this.interestStrategy = new SimpleInterestStrategy(); // Default strategy
    }

    public void setInterestStrategy(InterestCalculationStrategy interestStrategy) {
        this.interestStrategy = interestStrategy;
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            if (this.balance >= amount) {
                this.balance -= amount;
                System.out.println("Withdrawal of " + amount + " from account " + accountNumber + ". New balance: " + balance);
                notifyObservers("withdraw", amount);
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    @Override
    public void calculateInterest() {
        double interest = interestStrategy.calculateInterest(this.balance, this.interestRate);
        this.balance += interest;
        System.out.println("Interest of " + interest + " added to account " + accountNumber + ". New balance: " + balance);
        notifyObservers("interest_calculation", interest);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    public double getInterestRate() {
        return interestRate;
    }
}
