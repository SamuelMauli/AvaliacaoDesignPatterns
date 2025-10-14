package com.bank.dao;

import com.bank.config.DatabaseConfig;
import com.bank.model.Account;
import com.bank.model.Account.AccountType;
import com.bank.model.Account.InterestStrategyType;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public void addAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (id, user_id, account_number, account_type, balance, interest_strategy) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, account.getId());
            stmt.setObject(2, account.getUserId());
            stmt.setString(3, account.getAccountNumber());
            stmt.setString(4, account.getAccountType().name());
            stmt.setBigDecimal(5, account.getBalance());
            stmt.setString(6, account.getInterestStrategy() != null ? account.getInterestStrategy().name() : null);
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Account> getAccountById(UUID id) throws SQLException {
        String sql = "SELECT id, user_id, account_number, account_type, balance, interest_strategy FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAccount(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccountByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT id, user_id, account_number, account_type, balance, interest_strategy FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAccount(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Account> getAccountsByUserId(UUID userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, user_id, account_number, account_type, balance, interest_strategy FROM accounts WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        }
        return accounts;
    }

    @Override
    public void updateAccount(Account account) throws SQLException {
        String sql = "UPDATE accounts SET user_id = ?, account_number = ?, account_type = ?, balance = ?, interest_strategy = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, account.getUserId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setString(3, account.getAccountType().name());
            stmt.setBigDecimal(4, account.getBalance());
            stmt.setString(5, account.getInterestStrategy() != null ? account.getInterestStrategy().name() : null);
            stmt.setObject(6, account.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteAccount(UUID id) throws SQLException {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, user_id, account_number, account_type, balance, interest_strategy FROM accounts";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }
        return accounts;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String interestStrategyString = rs.getString("interest_strategy");
        InterestStrategyType interestStrategy = null;
        if (interestStrategyString != null) {
            interestStrategy = InterestStrategyType.valueOf(interestStrategyString);
        }
        return new Account(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("user_id"),
                rs.getString("account_number"),
                AccountType.valueOf(rs.getString("account_type")),
                rs.getBigDecimal("balance"),
                interestStrategy
        );
    }
}

