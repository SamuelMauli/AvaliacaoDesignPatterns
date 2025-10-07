package com.bank.account;

public class CheckingAccount extends Account implements Withdrawable {
    private double overdraftLimit;

    public CheckingAccount(String customerName, double initialBalance, double overdraftLimit) {
        super(customerName, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            if (this.balance + this.overdraftLimit >= amount) {
                this.balance -= amount;
                System.out.println("Withdrawal of " + amount + " from account " + accountNumber + ". New balance: " + balance);
                notifyObservers("withdraw", amount);
            } else {
                System.out.println("Insufficient funds and overdraft limit exceeded.");
            }
        } else {
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    @Override
    public String getAccountType() {
        return "Checking Account";
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}
