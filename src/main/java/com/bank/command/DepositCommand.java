package com.bank.command;

import com.bank.account.Account;
import com.bank.logger.TransactionLogger;

public class DepositCommand implements Command {
    private Account account;
    private double amount;
    private TransactionLogger logger;

    public DepositCommand(Account account, double amount) {
        this.account = account;
        this.amount = amount;
        this.logger = TransactionLogger.getInstance();
    }

    @Override
    public void execute() {
        account.deposit(amount);
        logger.log("Deposit: Account " + account.getAccountNumber() + ", Amount: " + amount + ", New Balance: " + account.getBalance());
    }
}
