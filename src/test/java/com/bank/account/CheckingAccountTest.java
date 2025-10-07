package com.bank.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckingAccountTest {

    private CheckingAccount account;

    @BeforeEach
    void setUp() {
        account = new CheckingAccount("John Doe", 1000.0, 500.0);
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
    void testWithdrawUsingOverdraft() {
        account.withdraw(1200.0); // 1000 balance - 1200 withdrawal = -200, within 500 overdraft
        assertEquals(-200.0, account.getBalance());
    }

    @Test
    void testWithdrawExceedingOverdraft() {
        account.withdraw(1600.0); // 1000 balance - 1600 withdrawal = -600, exceeds 500 overdraft
        assertEquals(1000.0, account.getBalance()); // Balance should not change
    }

    @Test
    void testWithdrawNegativeAmount() {
        account.withdraw(-100.0);
        assertEquals(1000.0, account.getBalance()); // Balance should not change
    }

    @Test
    void testGetAccountType() {
        assertEquals("Checking Account", account.getAccountType());
    }
}
