package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Account.AccountType;
import com.bank.model.Account.InterestStrategyType;
import com.bank.model.Transaction;
import com.bank.model.User;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BankingFacade {
    private AuthService authService;
    private AccountService accountService;

    public BankingFacade() {
        this.authService = new AuthService();
        this.accountService = new AccountService();
    }

    // User related operations (delegated to AuthService)
    public User registerUser(String username, String password, String name, String email) throws SQLException {
        return authService.register(username, password, name, email);
    }

    public Optional<User> loginUser(String username, String password) throws SQLException {
        return authService.login(username, password);
    }

    // Account related operations (delegated to AccountService)
    public Account createAccount(UUID userId, AccountType accountType, InterestStrategyType interestStrategy) throws SQLException {
        return accountService.createAccount(userId, accountType, interestStrategy);
    }

    public Optional<Account> getAccountById(UUID accountId) throws SQLException {
        return accountService.getAccountById(accountId);
    }

    public List<Account> getAccountsByUserId(UUID userId) throws SQLException {
        return accountService.getAccountsByUserId(userId);
    }

    public void deposit(UUID accountId, BigDecimal amount) throws SQLException {
        accountService.deposit(accountId, amount);
    }

    public void withdraw(UUID accountId, BigDecimal amount) throws SQLException {
        accountService.withdraw(accountId, amount);
    }

    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) throws SQLException {
        accountService.transfer(fromAccountId, toAccountId, amount);
    }

    public void applyInterest(UUID accountId) throws SQLException {
        accountService.applyInterest(accountId);
    }

    public List<Transaction> getAccountTransactions(UUID accountId) throws SQLException {
        return accountService.getAccountTransactions(accountId);
    }

    public List<com.bank.model.AuditLog> getAuditLogsByAccountId(UUID accountId) throws SQLException {
        return accountService.getAuditLogsByAccountId(accountId);
    }
}

