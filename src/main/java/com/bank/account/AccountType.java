package com.bank.account;

/**
 * Enum {@code AccountType} define os tipos de contas bancárias que podem ser criadas no sistema.
 * Utilizado em conjunto com o padrão **Factory Method** para determinar qual implementação
 * concreta de {@code Account} deve ser instanciada.
 */
public enum AccountType {
    /** Representa uma conta corrente. */
    CHECKING,
    /** Representa uma conta poupança. */
    SAVINGS
}
