package com.bank.service;

import com.bank.dao.AccountDAO;
import com.bank.dao.AccountDAOImpl;
import com.bank.dao.TransactionDAO;
import com.bank.dao.TransactionDAOImpl;
import com.bank.dao.AuditLogDAO;
import com.bank.dao.AuditLogDAOImpl;
import com.bank.model.Account;
import com.bank.model.Account.AccountType;
import com.bank.model.Account.InterestStrategyType;
import com.bank.model.Transaction;
import com.bank.model.Transaction.TransactionType;
import com.bank.model.AuditLog;
import com.bank.factory.AccountFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private AuditLogDAO auditLogDAO;

    public AccountService() {
        this.accountDAO = new AccountDAOImpl();
        this.transactionDAO = new TransactionDAOImpl();
        this.auditLogDAO = new AuditLogDAOImpl();
    }

    public Account createAccount(UUID userId, AccountType accountType, InterestStrategyType interestStrategy) throws SQLException {
        Account newAccount = AccountFactory.createAccount(userId, accountType, interestStrategy);
        accountDAO.addAccount(newAccount);
        return newAccount;
    }

    public Optional<Account> getAccountById(UUID accountId) throws SQLException {
        return accountDAO.getAccountById(accountId);
    }

    public List<Account> getAccountsByUserId(UUID userId) throws SQLException {
        return accountDAO.getAccountsByUserId(userId);
    }

    public void deposit(UUID accountId, BigDecimal amount) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        Account account = accountDAO.getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        account.setBalance(account.getBalance().add(amount));
        accountDAO.updateAccount(account);

        Transaction transaction = new Transaction(UUID.randomUUID(), accountId, TransactionType.DEPOSIT, amount, "Deposit", LocalDateTime.now());
        transactionDAO.addTransaction(transaction);
        auditLogDAO.addAuditLog(new AuditLog(UUID.randomUUID(), accountId, "DEPOSIT", "Deposit of " + amount, LocalDateTime.now()));
    }

    public void withdraw(UUID accountId, BigDecimal amount) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        Account account = accountDAO.getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountDAO.updateAccount(account);

        Transaction transaction = new Transaction(UUID.randomUUID(), accountId, TransactionType.WITHDRAWAL, amount, "Withdrawal", LocalDateTime.now());
        transactionDAO.addTransaction(transaction);
        auditLogDAO.addAuditLog(new AuditLog(UUID.randomUUID(), accountId, "WITHDRAWAL", "Withdrawal of " + amount, LocalDateTime.now()));
    }

    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Account fromAccount = accountDAO.getAccountById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found."));
        Account toAccount = accountDAO.getAccountById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found."));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in source account.");
        }

        // Perform transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountDAO.updateAccount(fromAccount);
        accountDAO.updateAccount(toAccount);

        // Record transactions
        Transaction fromTransaction = new Transaction(UUID.randomUUID(), fromAccountId, TransactionType.TRANSFER_OUT, amount, "Transfer to " + toAccount.getAccountNumber(), LocalDateTime.now());
        transactionDAO.addTransaction(fromTransaction);

        Transaction toTransaction = new Transaction(UUID.randomUUID(), toAccountId, TransactionType.TRANSFER_IN, amount, "Transfer from " + fromAccount.getAccountNumber(), LocalDateTime.now());
        transactionDAO.addTransaction(toTransaction);
        auditLogDAO.addAuditLog(new AuditLog(UUID.randomUUID(), fromAccountId, "TRANSFER_OUT", "Transfer of " + amount + " to " + toAccount.getAccountNumber(), LocalDateTime.now()));
        auditLogDAO.addAuditLog(new AuditLog(UUID.randomUUID(), toAccountId, "TRANSFER_IN", "Transfer of " + amount + " from " + fromAccount.getAccountNumber(), LocalDateTime.now()));
    }

    public void applyInterest(UUID accountId) throws SQLException {
        Account account = accountDAO.getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (account.getInterestStrategy() == null) {
            throw new IllegalStateException("Account does not have an interest strategy defined.");
        }

        InterestStrategy strategy = InterestStrategyFactory.createStrategy(account.getInterestStrategy());
        BigDecimal interest = strategy.calculateInterest(account.getBalance());

        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            account.setBalance(account.getBalance().add(interest));
            accountDAO.updateAccount(account);

            Transaction interestTransaction = new Transaction(UUID.randomUUID(), accountId, TransactionType.INTEREST, interest, "Interest Applied", LocalDateTime.now());
            transactionDAO.addTransaction(interestTransaction);
            auditLogDAO.addAuditLog(new AuditLog(UUID.randomUUID(), accountId, "INTEREST", "Interest Applied: " + interest, LocalDateTime.now()));
        }
    }

    public List<Transaction> getAccountTransactions(UUID accountId) throws SQLException {
        return transactionDAO.getTransactionsByAccountId(accountId);
    }

    public List<AuditLog> getAuditLogsByAccountId(UUID accountId) throws SQLException {
        return auditLogDAO.getAuditLogsByAccountId(accountId);
    }
}

