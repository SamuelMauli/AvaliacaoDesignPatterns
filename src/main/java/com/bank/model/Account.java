package com.bank.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    private UUID id;
    private UUID userId;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private InterestStrategyType interestStrategy;

    public Account(UUID id, UUID userId, String accountNumber, AccountType accountType, BigDecimal balance, InterestStrategyType interestStrategy) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.interestStrategy = interestStrategy;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public InterestStrategyType getInterestStrategy() {
        return interestStrategy;
    }

    // Setters
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setInterestStrategy(InterestStrategyType interestStrategy) {
        this.interestStrategy = interestStrategy;
    }

    @Override
    public String toString() {
        return "Account{" +
               "id=" + id +
               ", userId=" + userId +
               ", accountNumber=\"" + accountNumber + "\"" +
               ", accountType=" + accountType +
               ", balance=" + balance +
               ", interestStrategy=" + interestStrategy +
               "}";
    }

    public enum AccountType {
        CHECKING,
        SAVINGS
    }

    public enum InterestStrategyType {
        SIMPLE,
        HIGH_YIELD
    }
}

