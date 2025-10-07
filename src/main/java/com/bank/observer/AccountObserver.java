package com.bank.observer;

import com.bank.account.Account;

public interface AccountObserver {
    void update(Account account, String eventType, double amount);
}
