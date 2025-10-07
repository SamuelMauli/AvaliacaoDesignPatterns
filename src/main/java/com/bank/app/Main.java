package com.bank.app;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.SavingsAccount;
import com.bank.decorator.OverdraftProtectionDecorator;
import com.bank.facade.BankingFacade;
import com.bank.observer.AuditService;
import com.bank.strategy.HighYieldInterestStrategy;
import com.bank.logger.TransactionLogger;

public class Main {
    public static void main(String[] args) {
        // Initialize the Banking Facade
        BankingFacade bankingFacade = new BankingFacade();

        // Initialize Audit Service and attach it to accounts
        AuditService auditService = new AuditService();

        // Create a Checking Account
        System.out.println("\n--- Creating Checking Account ---");
        String checkingAccNum = bankingFacade.createAccount(AccountType.CHECKING, "Alice Smith", 1000.0, 500.0);
        Account aliceChecking = bankingFacade.getAccount(checkingAccNum);
        if (aliceChecking != null) {
            aliceChecking.addObserver(auditService);
        }

        // Create a Savings Account
        System.out.println("\n--- Creating Savings Account ---");
        String savingsAccNum = bankingFacade.createAccount(AccountType.SAVINGS, "Bob Johnson", 2000.0, 0.05);
        Account bobSavings = bankingFacade.getAccount(savingsAccNum);
        if (bobSavings != null) {
            bobSavings.addObserver(auditService);
        }

        // Demonstrate Deposit
        System.out.println("\n--- Demonstrating Deposit ---");
        bankingFacade.deposit(checkingAccNum, 200.0);
        bankingFacade.deposit(savingsAccNum, 500.0);

        // Demonstrate Withdrawal
        System.out.println("\n--- Demonstrating Withdrawal ---");
        bankingFacade.withdraw(checkingAccNum, 300.0);
        bankingFacade.withdraw(savingsAccNum, 1000.0);

        // Demonstrate Overdraft Protection (Decorator Pattern)
        System.out.println("\n--- Demonstrating Overdraft Protection ---");
        // Let's say Alice's checking account now has 900.0 (1000 + 200 - 300)
        // Try to withdraw more than balance but within overdraft limit
        bankingFacade.withdraw(checkingAccNum, 1000.0); // Should use overdraft

        // Create a new Checking Account and apply OverdraftProtectionDecorator explicitly
        System.out.println("\n--- Creating a new account with Overdraft Protection Decorator ---");
        String decoratedAccNum = bankingFacade.createAccount(AccountType.CHECKING, "Charlie Brown", 500.0, 0.0);
        Account charlieChecking = bankingFacade.getAccount(decoratedAccNum);
        if (charlieChecking != null) {
            charlieChecking.addObserver(auditService);
            OverdraftProtectionDecorator decoratedCharlie = new OverdraftProtectionDecorator(charlieChecking, 200.0);
            // Now use the decorated account for operations that involve withdrawal logic
            System.out.println("Attempting withdrawal on decorated account...");
            decoratedCharlie.withdraw(600.0); // 500 balance + 200 overdraft = 700 limit
            System.out.println("Charlie's decorated account balance: " + decoratedCharlie.getBalance());
            decoratedCharlie.withdraw(200.0); // Should be -100, within -200 limit
            System.out.println("Charlie's decorated account balance: " + decoratedCharlie.getBalance());
            decoratedCharlie.withdraw(100.0); // Should fail, -200 limit exceeded
        }

        // Demonstrate Interest Calculation (Strategy Pattern)
        System.out.println("\n--- Demonstrating Interest Calculation ---");
        if (bobSavings instanceof SavingsAccount) {
            SavingsAccount bobSavingsAccount = (SavingsAccount) bobSavings;
            System.out.println("Bob's savings account balance before interest: " + bobSavingsAccount.getBalance());
            bobSavingsAccount.calculateInterest();
            System.out.println("Bob's savings account balance after interest: " + bobSavingsAccount.getBalance());

            // Change strategy
            System.out.println("\n--- Changing Interest Calculation Strategy ---");
            bobSavingsAccount.setInterestStrategy(new HighYieldInterestStrategy());
            System.out.println("Bob's savings account balance before high yield interest: " + bobSavingsAccount.getBalance());
            bobSavingsAccount.calculateInterest();
            System.out.println("Bob's savings account balance after high yield interest: " + bobSavingsAccount.getBalance());
        }

        // Verify Audit Log (Observer Pattern and Singleton Logger)
        System.out.println("\n--- Verifying Audit Log (Check transactions.log file) ---");
        // The log file 'transactions.log' will contain all logged events.
        // In a real application, you would read and display its content.

        // Close the logger to ensure all messages are flushed
        TransactionLogger.getInstance().close();
    }
}
