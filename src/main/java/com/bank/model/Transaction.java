package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private UUID id;
    private UUID accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;

    public Transaction(UUID id, UUID accountId, TransactionType transactionType, BigDecimal amount, String description, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters (if needed)
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", accountId=" + accountId +
               ", transactionType=" + transactionType +
               ", amount=" + amount +
               ", description=\"" + description + "\"" +
               ", createdAt=" + createdAt +
               "}";
    }

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_IN,
        TRANSFER_OUT,
        INTEREST
    }
}

