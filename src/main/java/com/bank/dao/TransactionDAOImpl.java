package com.bank.dao;

import com.bank.config.DatabaseConfig;
import com.bank.model.Transaction;
import com.bank.model.Transaction.TransactionType;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public void addTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (id, account_id, transaction_type, amount, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, transaction.getId());
            stmt.setObject(2, transaction.getAccountId());
            stmt.setString(3, transaction.getTransactionType().name());
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setString(5, transaction.getDescription());
            stmt.setTimestamp(6, Timestamp.valueOf(transaction.getCreatedAt()));
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Transaction> getTransactionById(UUID id) throws SQLException {
        String sql = "SELECT id, account_id, transaction_type, amount, description, created_at FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(UUID accountId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, transaction_type, amount, description, created_at FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("account_id"),
                TransactionType.valueOf(rs.getString("transaction_type")),
                rs.getBigDecimal("amount"),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}

