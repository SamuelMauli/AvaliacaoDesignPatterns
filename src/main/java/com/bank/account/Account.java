package com.bank.account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.bank.observer.AccountObserver;

public abstract class Account implements Depositable {
    protected String accountNumber;
    protected double balance;
    protected String customerName;
    private List<AccountObserver> observers = new ArrayList<>();

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

    public void addObserver(AccountObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AccountObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(String eventType, double amount) {
        for (AccountObserver observer : observers) {
            observer.update(this, eventType, amount);
        }
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            System.out.println("Deposit of " + amount + " to account " + accountNumber + ". New balance: " + balance);
            notifyObservers("deposit", amount);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    public abstract String getAccountType();
}
