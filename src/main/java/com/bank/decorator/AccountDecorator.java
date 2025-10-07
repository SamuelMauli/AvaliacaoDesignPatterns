package com.bank.decorator;

import com.bank.account.Account;
import com.bank.account.Depositable;
import com.bank.account.Withdrawable;
import com.bank.observer.AccountObserver;

/**
 * Classe abstrata {@code AccountDecorator} implementa o padrão de projeto **Decorator**.
 * Ela serve como a base para todos os decoradores de conta, permitindo adicionar
 * novas funcionalidades a um objeto {@code Account} existente dinamicamente, sem
 * alterar sua estrutura original.
 *
 * <p>O padrão Decorator é útil para estender o comportamento de objetos de forma flexível,
 * aderindo ao **Princípio Aberto/Fechado (OCP)**: a classe {@code Account} é fechada para
 * modificação, mas aberta para extensão através de decoradores. Isso evita a explosão
 * de subclasses que ocorreria se cada combinação de funcionalidades fosse implementada
 * via herança.
 *
 * <p>Esta classe também implementa {@code Depositable} e {@code Withdrawable} para garantir
 * que os decoradores possam ser usados onde uma {@code Account} ou uma conta que suporta
 * depósito/saque é esperada, mantendo o **Princípio da Substituição de Liskov (LSP)**.
 */
public abstract class AccountDecorator extends Account implements Depositable, Withdrawable {
    protected Account decoratedAccount;

    /**
     * Construtor para {@code AccountDecorator}.
     * @param decoratedAccount A conta a ser decorada.
     */
    public AccountDecorator(Account decoratedAccount) {
        // O construtor da classe base (Account) é chamado com os dados da conta decorada.
        // Isso garante que o decorador tenha as informações básicas da conta.
        super(decoratedAccount.getCustomerName(), decoratedAccount.getBalance());
        this.decoratedAccount = decoratedAccount;
        // Garante que o decorador use o mesmo número de conta da conta decorada para consistência.
        this.accountNumber = decoratedAccount.getAccountNumber();
    }

    /**
     * Delega a operação de depósito para a conta decorada.
     * @param amount O valor a ser depositado.
     */
    @Override
    public void deposit(double amount) {
        decoratedAccount.deposit(amount);
    }

    /**
     * Delega a operação de saque para a conta decorada, se ela for {@code Withdrawable}.
     * Subclasses de decoradores podem sobrescrever este método para adicionar lógica
     * específica antes ou depois do saque, ou para modificar o comportamento do saque.
     * @param amount O valor a ser sacado.
     */
    @Override
    public void withdraw(double amount) {
        if (decoratedAccount instanceof Withdrawable) {
            ((Withdrawable) decoratedAccount).withdraw(amount);
        } else {
            System.out.println("Withdrawal not supported by the decorated account.");
        }
    }

    /**
     * Retorna o saldo da conta decorada.
     * @return O saldo da conta decorada.
     */
    @Override
    public double getBalance() {
        return decoratedAccount.getBalance();
    }

    /**
     * Retorna o tipo da conta decorada. Decoradores podem estender esta descrição.
     * @return O tipo da conta decorada.
     */
    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType();
    }

    /**
     * Delega a adição de observadores para a conta decorada.
     * @param observer O observador a ser adicionado.
     */
    @Override
    public void addObserver(AccountObserver observer) {
        decoratedAccount.addObserver(observer);
    }

    /**
     * Delega a remoção de observadores para a conta decorada.
     * @param observer O observador a ser removido.
     */
    @Override
    public void removeObserver(AccountObserver observer) {
        decoratedAccount.removeObserver(observer);
    }

    /**
     * Delega a notificação de observadores para a conta decorada.
     * @param eventType O tipo de evento.
     * @param amount O valor associado ao evento.
     */
    @Override
    public void notifyObservers(String eventType, double amount) {
        decoratedAccount.notifyObservers(eventType, amount);
    }

    /**
     * Delega o ajuste de saldo e notificação para a conta decorada.
     * @param amount O valor pelo qual o saldo será ajustado.
     * @param eventType O tipo de evento.
     */
    @Override
    public void adjustBalanceAndNotify(double amount, String eventType) {
        decoratedAccount.adjustBalanceAndNotify(amount, eventType);
    }
}
