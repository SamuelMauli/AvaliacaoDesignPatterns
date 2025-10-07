package com.bank.account;

public class SavingsAccount extends Account implements Withdrawable, InterestBearing {
    private double interestRate;

    public SavingsAccount(String customerName, double initialBalance, double interestRate) {
        super(customerName, initialBalance);
        this.interestRate = interestRate;
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            if (this.balance >= amount) {
                this.balance -= amount;
                System.out.println("Withdrawal of " + amount + " from account " + accountNumber + ". New balance: " + balance);
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    @Override
    public void calculateInterest() {
        double interest = this.balance * interestRate;
        this.balance += interest;
        System.out.println("Interest of " + interest + " added to account " + accountNumber + ". New balance: " + balance);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    public double getInterestRate() {
        return interestRate;
    }
}
