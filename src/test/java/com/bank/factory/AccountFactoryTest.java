package com.bank.factory;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.CheckingAccount;
import com.bank.account.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountFactoryTest {

    @Test
    void testCreateCheckingAccount() {
        Account account = AccountFactory.createAccount(AccountType.CHECKING, "Test Customer", 100.0, 50.0);
        assertNotNull(account);
        assertTrue(account instanceof CheckingAccount);
        assertEquals("Test Customer", account.getCustomerName());
        assertEquals(100.0, account.getBalance());
        assertEquals(50.0, ((CheckingAccount) account).getOverdraftLimit());
    }

    @Test
    void testCreateSavingsAccount() {
        Account account = AccountFactory.createAccount(AccountType.SAVINGS, "Test Customer", 200.0, 0.02);
        assertNotNull(account);
        assertTrue(account instanceof SavingsAccount);
        assertEquals("Test Customer", account.getCustomerName());
        assertEquals(200.0, account.getBalance());
        assertEquals(0.02, ((SavingsAccount) account).getInterestRate());
    }

    @Test
    void testCreateAccountWithInvalidType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AccountFactory.createAccount(null, "Test Customer", 100.0);
        });
        assertEquals("Unknown account type: null", exception.getMessage());
    }
}
