package com.bank.account;

import java.util.UUID;

public abstract class Account implements Depositable {
    protected String accountNumber;
    protected double balance;
    protected String customerName;

    public Account(String customerName, double initialBalance) {
        this.accountNumber = UUID.randomUUID().toString();
        this.customerName = customerName;
        this.balance = initialBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Deposit of " + amount + " to account " + accountNumber + ". New balance: " + balance);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    public abstract String getAccountType();
}
