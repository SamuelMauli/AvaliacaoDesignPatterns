package com.bank.decorator;

import com.bank.account.Account;
import com.bank.account.Depositable;
import com.bank.account.Withdrawable;

public abstract class AccountDecorator extends Account implements Depositable, Withdrawable {
    protected Account decoratedAccount;

    public AccountDecorator(Account decoratedAccount) {
        super(decoratedAccount.getCustomerName(), decoratedAccount.getBalance());
        this.decoratedAccount = decoratedAccount;
        // Ensure the decorator uses the same account number as the decorated account
        this.accountNumber = decoratedAccount.getAccountNumber();
    }

    @Override
    public void deposit(double amount) {
        decoratedAccount.deposit(amount);
    }

    @Override
    public void withdraw(double amount) {
        if (decoratedAccount instanceof Withdrawable) {
            ((Withdrawable) decoratedAccount).withdraw(amount);
        } else {
            System.out.println("Withdrawal not supported by the decorated account.");
        }
    }

    @Override
    public double getBalance() {
        return decoratedAccount.getBalance();
    }

    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType();
    }
}
