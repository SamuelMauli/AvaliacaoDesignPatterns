package com.bank.account;

import com.bank.strategy.SimpleInterestStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    private SavingsAccount account;

    @BeforeEach
    void setUp() {
        account = new SavingsAccount("Jane Doe", 1000.0, 0.05);
    }

    @Test
    void testDeposit() {
        account.deposit(200.0);
        assertEquals(1200.0, account.getBalance());
    }

    @Test
    void testDepositNegativeAmount() {
        account.deposit(-100.0);
        assertEquals(1000.0, account.getBalance()); // Balance should not change
    }

    @Test
    void testWithdrawSufficientFunds() {
        account.withdraw(300.0);
        assertEquals(700.0, account.getBalance());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        account.withdraw(1200.0);
        assertEquals(1000.0, account.getBalance()); // Balance should not change
    }

    @Test
    void testWithdrawNegativeAmount() {
        account.withdraw(-100.0);
        assertEquals(1000.0, account.getBalance()); // Balance should not change
    }

    @Test
    void testCalculateInterest() {
        account.calculateInterest();
        assertEquals(1000.0 * (1 + 0.05), account.getBalance(), 0.001);
    }

    @Test
    void testSetInterestStrategy() {
        account.setInterestStrategy(new SimpleInterestStrategy());
        account.calculateInterest();
        assertEquals(1000.0 * (1 + 0.05), account.getBalance(), 0.001);
    }

    @Test
    void testGetAccountType() {
        assertEquals("Savings Account", account.getAccountType());
    }
}
