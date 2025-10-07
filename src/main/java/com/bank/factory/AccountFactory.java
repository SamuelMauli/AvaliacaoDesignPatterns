package com.bank.factory;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.CheckingAccount;
import com.bank.account.SavingsAccount;

public class AccountFactory {
    public static Account createAccount(AccountType type, String customerName, double initialBalance, double... params) {
        switch (type) {
            case CHECKING:
                // params[0] would be overdraftLimit
                return new CheckingAccount(customerName, initialBalance, params.length > 0 ? params[0] : 0.0);
            case SAVINGS:
                // params[0] would be interestRate
                return new SavingsAccount(customerName, initialBalance, params.length > 0 ? params[0] : 0.0);
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
}
