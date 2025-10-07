package com.bank.observer;

import com.bank.account.Account;
import com.bank.logger.TransactionLogger;

public class AuditService implements AccountObserver {
    private TransactionLogger logger;

    public AuditService() {
        this.logger = TransactionLogger.getInstance();
    }

    @Override
    public void update(Account account, String eventType, double amount) {
        logger.log("AUDIT: Account " + account.getAccountNumber() + ", Event: " + eventType + ", Amount: " + amount + ", Current Balance: " + account.getBalance());
    }
}
