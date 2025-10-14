package com.bank.dao;

import com.bank.model.Transaction;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDAO {
    void addTransaction(Transaction transaction) throws SQLException;
    Optional<Transaction> getTransactionById(UUID id) throws SQLException;
    List<Transaction> getTransactionsByAccountId(UUID accountId) throws SQLException;
}

