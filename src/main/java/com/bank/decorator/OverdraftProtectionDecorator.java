package com.bank.decorator;

import com.bank.account.Account;
import com.bank.account.Withdrawable;

/**
 * Classe {@code OverdraftProtectionDecorator} estende {@code AccountDecorator} para adicionar
 * a funcionalidade de proteção contra cheque especial a uma conta bancária.
 *
 * <p>Este decorador permite que uma conta realize saques mesmo que o saldo seja insuficiente,
 * desde que o valor do saque não exceda o saldo atual mais o limite de cheque especial.
 *
 * <p>A aplicação do padrão **Decorator** aqui demonstra como novas responsabilidades
 * (como a proteção contra cheque especial) podem ser adicionadas a objetos de conta
 * dinamicamente, sem modificar as classes de conta existentes. Isso adere ao
 * **Princípio Aberto/Fechado (OCP)**, pois a funcionalidade é estendida sem modificar
 * o código-fonte da {@code Account} ou de suas subclasses concretas.
 *
 * <p>A lógica de saque é sobrescrita para incluir a verificação do limite de cheque especial.
 * Se o saque for permitido, o saldo da conta decorada é ajustado e os observadores são notificados.
 */
public class OverdraftProtectionDecorator extends AccountDecorator {
    private double overdraftLimit;

    /**
     * Construtor para criar um decorador de proteção contra cheque especial.
     * @param decoratedAccount A conta a ser decorada com proteção contra cheque especial.
     * @param overdraftLimit O limite de cheque especial permitido por este decorador.
     */
    public OverdraftProtectionDecorator(Account decoratedAccount, double overdraftLimit) {
        super(decoratedAccount);
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Sobrescreve o método de saque para incluir a lógica de proteção contra cheque especial.
     * Permite saques que excedam o saldo atual até o limite de cheque especial configurado
     * para este decorador.
     * @param amount O valor a ser sacado.
     */
    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            // Verifica se o saque é possível considerando o saldo atual da conta decorada
            // mais o limite de cheque especial fornecido por este decorador.
            if (decoratedAccount.getBalance() + this.overdraftLimit >= amount) {
                // Ajusta o saldo da conta decorada e notifica os observadores.
                // Usamos o método performBalanceAdjustment da conta decorada para garantir
                // que a lógica de notificação e ajuste de saldo seja consistente.
                decoratedAccount.adjustBalanceAndNotify(-amount, "withdraw_with_overdraft");
                System.out.println("Withdrawal of " + amount + " from account " + decoratedAccount.getAccountNumber() + " with overdraft protection. New balance: " + decoratedAccount.getBalance());
            } else {
                System.out.println("Insufficient funds and overdraft limit exceeded for account " + decoratedAccount.getAccountNumber());
            }
        } else {
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    /**
     * Retorna o tipo da conta, indicando que ela possui proteção contra cheque especial.
     * @return Uma string descrevendo o tipo da conta com a funcionalidade de proteção contra cheque especial.
     */
    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType() + " with Overdraft Protection";
    }
}
