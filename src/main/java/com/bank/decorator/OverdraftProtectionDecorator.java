package com.bank.decorator;

import com.bank.account.Account;
import com.bank.account.Withdrawable;

public class OverdraftProtectionDecorator extends AccountDecorator {
    private double overdraftLimit;

    public OverdraftProtectionDecorator(Account decoratedAccount, double overdraftLimit) {
        super(decoratedAccount);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            // Check if the withdrawal is possible with the current balance + this decorator's overdraft limit
            if (decoratedAccount.getBalance() + this.overdraftLimit >= amount) {
                // Directly modify balance of the decorated account using the protected adjustBalanceAndNotify method
                // This ensures that the balance is updated and observers are notified.
                decoratedAccount.adjustBalanceAndNotify(-amount, "withdraw_with_overdraft");
                System.out.println("Withdrawal of " + amount + " from account " + decoratedAccount.getAccountNumber() + " with overdraft protection. New balance: " + decoratedAccount.getBalance());
            } else {
                System.out.println("Insufficient funds and overdraft limit exceeded for account " + decoratedAccount.getAccountNumber());
            }
        } else {
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType() + " with Overdraft Protection";
    }
}
