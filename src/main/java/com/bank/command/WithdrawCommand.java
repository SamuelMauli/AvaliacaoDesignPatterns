package com.bank.command;

import com.bank.account.Withdrawable;
import com.bank.account.Account;
import com.bank.logger.TransactionLogger;

public class WithdrawCommand implements Command {
    private Withdrawable account;
    private Account baseAccount;
    private double amount;
    private TransactionLogger logger;

    public WithdrawCommand(Withdrawable account, Account baseAccount, double amount) {
        this.account = account;
        this.baseAccount = baseAccount;
        this.amount = amount;
        this.logger = TransactionLogger.getInstance();
    }

    @Override
    public void execute() {
        account.withdraw(amount);
        logger.log("Withdrawal: Account " + baseAccount.getAccountNumber() + ", Amount: " + amount + ", New Balance: " + baseAccount.getBalance());
    }
}
