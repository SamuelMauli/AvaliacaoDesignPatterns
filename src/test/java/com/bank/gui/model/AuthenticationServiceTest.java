package com.bank.gui.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para {@code AuthenticationService}.
 * Testa a funcionalidade de autenticação de usuários.
 */
public class AuthenticationServiceTest {

    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService();
    }

    @Test
    void testAuthenticateValidUser() {
        // O serviço de autenticação tem usuários hardcoded para demonstração
        assertTrue(authService.authenticate("user1", "pass1"));
        assertTrue(authService.authenticate("admin", "admin"));
    }

    @Test
    void testAuthenticateInvalidUsername() {
        assertFalse(authService.authenticate("nonexistent", "pass1"));
    }

    @Test
    void testAuthenticateInvalidPassword() {
        assertFalse(authService.authenticate("user1", "wrongpass"));
    }

    @Test
    void testAuthenticateNullUsername() {
        assertFalse(authService.authenticate(null, "pass1"));
    }

    @Test
    void testAuthenticateNullPassword() {
        assertFalse(authService.authenticate("user1", null));
    }

    @Test
    void testAuthenticateEmptyUsername() {
        assertFalse(authService.authenticate("", "pass1"));
    }

    @Test
    void testAuthenticateEmptyPassword() {
        assertFalse(authService.authenticate("user1", ""));
    }
}
