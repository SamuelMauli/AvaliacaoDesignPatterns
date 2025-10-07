package com.bank.facade;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.command.Command;
import com.bank.command.DepositCommand;
import com.bank.command.WithdrawCommand;
import com.bank.factory.AccountFactory;
import com.bank.account.Withdrawable;

import java.util.HashMap;
import java.util.Map;

public class BankingFacade {
    private Map<String, Account> accounts;

    public BankingFacade() {
        this.accounts = new HashMap<>();
    }

    public String createAccount(AccountType type, String customerName, double initialBalance, double... params) {
        Account account = AccountFactory.createAccount(type, customerName, initialBalance, params);
        accounts.put(account.getAccountNumber(), account);
        System.out.println("Account created: " + account.getAccountType() + " for " + customerName + " with account number " + account.getAccountNumber());
        return account.getAccountNumber();
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public void deposit(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            Command deposit = new DepositCommand(account, amount);
            deposit.execute();
        } else {
            System.out.println("Account not found: " + accountNumber);
        }
    }

    public void withdraw(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account instanceof Withdrawable) {
            Command withdraw = new WithdrawCommand((Withdrawable) account, account, amount);
            withdraw.execute();
        } else if (account != null) {
            System.out.println("Withdrawal not supported for this account type: " + account.getAccountType());
        } else {
            System.out.println("Account not found: " + accountNumber);
        }
    }

    public double getBalance(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            return account.getBalance();
        } else {
            System.out.println("Account not found: " + accountNumber);
            return -1.0; // Indicate error
        }
    }
}
