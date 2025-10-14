package com.bank.dao;

import com.bank.model.Account;
import com.bank.model.Account.AccountType;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountDAO {
    void addAccount(Account account) throws SQLException;
    Optional<Account> getAccountById(UUID id) throws SQLException;
    Optional<Account> getAccountByNumber(String accountNumber) throws SQLException;
    List<Account> getAccountsByUserId(UUID userId) throws SQLException;
    void updateAccount(Account account) throws SQLException;
    void deleteAccount(UUID id) throws SQLException;
    List<Account> getAllAccounts() throws SQLException;
}

