package com.bank.facade;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.CheckingAccount;
import com.bank.account.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para {@code BankingFacade}.
 * Testa a integração dos subsistemas através da Facade, garantindo que as operações
 * de alto nível funcionem conforme o esperado.
 */
public class BankingFacadeTest {

    private BankingFacade bankingFacade;

    @BeforeEach
    void setUp() {
        bankingFacade = new BankingFacade();
    }

    @Test
    void testCreateAccountAndGetAccount() {
        String accNum1 = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        assertNotNull(accNum1);
        Account account1 = bankingFacade.getAccount(accNum1);
        assertNotNull(account1);
        assertTrue(account1 instanceof CheckingAccount);
        assertEquals("Alice", account1.getCustomerName());
        assertEquals(1000.0, account1.getBalance());

        String accNum2 = bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 2000.0, 0.03);
        assertNotNull(accNum2);
        Account account2 = bankingFacade.getAccount(accNum2);
        assertNotNull(account2);
        assertTrue(account2 instanceof SavingsAccount);
        assertEquals("Bob", account2.getCustomerName());
        assertEquals(2000.0, account2.getBalance());
    }

    @Test
    void testDeposit() {
        String accNum = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        bankingFacade.deposit(accNum, 200.0);
        assertEquals(1200.0, bankingFacade.getBalance(accNum));
    }

    @Test
    void testWithdraw() {
        String accNum = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        bankingFacade.withdraw(accNum, 300.0);
        assertEquals(700.0, bankingFacade.getBalance(accNum));
    }

    @Test
    void testWithdrawWithOverdraft() {
        String accNum = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        bankingFacade.withdraw(accNum, 1200.0);
        assertEquals(-200.0, bankingFacade.getBalance(accNum));
    }

    @Test
    void testWithdrawExceedingOverdraft() {
        String accNum = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        bankingFacade.withdraw(accNum, 1600.0); // Exceeds 1000 + 500
        assertEquals(1000.0, bankingFacade.getBalance(accNum)); // Should not change
    }

    @Test
    void testGetAllAccountNumbers() {
        bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 2000.0, 0.03);
        assertEquals(2, bankingFacade.getAllAccountNumbers().size());
    }

    @Test
    void testGetAllAccounts() {
        bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 2000.0, 0.03);
        assertEquals(2, bankingFacade.getAllAccounts().size());
        assertTrue(bankingFacade.getAllAccounts().containsKey(bankingFacade.getAllAccountNumbers().get(0)));
    }

    @Test
    void testAccountExists() {
        String accNum = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        assertTrue(bankingFacade.accountExists(accNum));
        assertFalse(bankingFacade.accountExists("non-existent"));
    }

    @Test
    void testGetTotalAccountsCount() {
        bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        assertEquals(1, bankingFacade.getTotalAccountsCount());
        bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 2000.0, 0.03);
        assertEquals(2, bankingFacade.getTotalAccountsCount());
    }

    @Test
    void testTransferSufficientFunds() {
        String accNum1 = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        String accNum2 = bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 500.0, 0.03);

        assertTrue(bankingFacade.transfer(accNum1, accNum2, 200.0));
        assertEquals(800.0, bankingFacade.getBalance(accNum1));
        assertEquals(700.0, bankingFacade.getBalance(accNum2));
    }

    @Test
    void testTransferInsufficientFunds() {
        String accNum1 = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 100.0, 0.0);
        String accNum2 = bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 500.0, 0.03);

        assertFalse(bankingFacade.transfer(accNum1, accNum2, 200.0));
        assertEquals(100.0, bankingFacade.getBalance(accNum1));
        assertEquals(500.0, bankingFacade.getBalance(accNum2));
    }

    @Test
    void testTransferWithOverdraft() {
        String accNum1 = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 100.0, 200.0);
        String accNum2 = bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 500.0, 0.03);

        assertTrue(bankingFacade.transfer(accNum1, accNum2, 200.0)); // 100 - 200 = -100, within 200 overdraft
        assertEquals(-100.0, bankingFacade.getBalance(accNum1));
        assertEquals(700.0, bankingFacade.getBalance(accNum2));
    }

    @Test
    void testTransferExceedingOverdraft() {
        String accNum1 = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 100.0, 50.0);
        String accNum2 = bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 500.0, 0.03);

        assertFalse(bankingFacade.transfer(accNum1, accNum2, 200.0)); // 100 - 200 = -100, exceeds 50 overdraft
        assertEquals(100.0, bankingFacade.getBalance(accNum1));
        assertEquals(500.0, bankingFacade.getBalance(accNum2));
    }

    @Test
    void testTransferToNonExistentAccount() {
        String accNum1 = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        assertFalse(bankingFacade.transfer(accNum1, "non-existent", 100.0));
        assertEquals(1000.0, bankingFacade.getBalance(accNum1));
    }

    @Test
    void testTransferFromNonExistentAccount() {
        String accNum2 = bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 500.0, 0.03);
        assertFalse(bankingFacade.transfer("non-existent", accNum2, 100.0));
        assertEquals(500.0, bankingFacade.getBalance(accNum2));
    }

    @Test
    void testTransferNegativeAmount() {
        String accNum1 = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        String accNum2 = bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 500.0, 0.03);
        assertFalse(bankingFacade.transfer(accNum1, accNum2, -100.0));
        assertEquals(1000.0, bankingFacade.getBalance(accNum1));
        assertEquals(500.0, bankingFacade.getBalance(accNum2));
    }

    @Test
    void testTransferZeroAmount() {
        String accNum1 = bankingFacade.createAccount(AccountType.CHECKING, "Alice", 1000.0, 500.0);
        String accNum2 = bankingFacade.createAccount(AccountType.SAVINGS, "Bob", 500.0, 0.03);
        assertFalse(bankingFacade.transfer(accNum1, accNum2, 0.0));
        assertEquals(1000.0, bankingFacade.getBalance(accNum1));
        assertEquals(500.0, bankingFacade.getBalance(accNum2));
    }
}

