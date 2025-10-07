package com.bank.account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.bank.observer.AccountObserver;

/**
 * Classe abstrata {@code Account} serve como a base para todos os tipos de contas bancárias.
 * Ela encapsula atributos comuns como número da conta, saldo e nome do cliente, e fornece
 * a funcionalidade básica de depósito. Além disso, implementa o padrão **Observer** para
 * notificar partes interessadas sobre mudanças no estado da conta.
 *
 * <p>Esta classe adere a vários princípios SOLID:
 * <ul>
 *     <li>**Princípio da Responsabilidade Única (SRP)**: A classe {@code Account} é primariamente
 *         responsável por gerenciar o estado básico de uma conta (saldo, número, cliente) e a lógica
 *         de depósito. Outras responsabilidades, como saque ou cálculo de juros, são delegadas
 *         a interfaces específicas ou subclasses.</li>
 *     <li>**Princípio Aberto/Fechado (OCP)**: A classe é aberta para extensão (novos tipos de conta
 *         podem estender {@code Account}) mas fechada para modificação (o código cliente que opera
 *         em {@code Account} não precisa ser alterado para suportar novos tipos de conta).</li>
 *     <li>**Princípio da Substituição de Liskov (LSP)**: Subtipos de {@code Account} (como
 *         {@code CheckingAccount} e {@code SavingsAccount}) devem ser substituíveis por {@code Account}
 *         sem alterar a correção do programa. Métodos como {@code deposit} são implementados de forma
 *         consistente.</li>
 *     <li>**Princípio da Inversão de Dependência (DIP)**: A classe depende de abstrações ({@code AccountObserver})
 *         em vez de implementações concretas, permitindo um acoplamento fraco.</li>
 * </ul>
 */
public abstract class Account implements Depositable {
    protected String accountNumber;
    protected double balance;
    protected String customerName;
    private List<AccountObserver> observers = new ArrayList<>();

    /**
     * Construtor para criar uma nova conta.
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     */
    public Account(String customerName, double initialBalance) {
        this.accountNumber = UUID.randomUUID().toString(); // Gera um número de conta único
        this.customerName = customerName;
        this.balance = initialBalance;
    }

    /**
     * Retorna o número da conta.
     * @return O número da conta.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Retorna o saldo atual da conta.
     * @return O saldo da conta.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Retorna o nome do titular da conta.
     * @return O nome do titular da conta.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Adiciona um observador à lista de observadores da conta.
     * @param observer O observador a ser adicionado.
     */
    public void addObserver(AccountObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove um observador da lista de observadores da conta.
     * @param observer O observador a ser removido.
     */
    public void removeObserver(AccountObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica todos os observadores registrados sobre um evento na conta.
     * @param eventType O tipo de evento (ex: "deposit", "withdraw").
     * @param amount O valor associado ao evento.
     */
    public void notifyObservers(String eventType, double amount) {
        for (AccountObserver observer : observers) {
            observer.update(this, eventType, amount);
        }
    }

    /**
     * Método protegido para ajustar o saldo da conta e notificar os observadores.
     * Este método é utilizado internamente por subclasses e decoradores para modificar o saldo
     * de forma controlada e garantir que as notificações sejam sempre enviadas.
     * @param amount O valor pelo qual o saldo será ajustado (positivo para aumento, negativo para diminuição).
     * @param eventType O tipo de evento a ser notificado.
     */
    public void adjustBalanceAndNotify(double amount, String eventType) {
        this.balance += amount;
        notifyObservers(eventType, amount);
    }

    /**
     * Implementação da operação de depósito. O valor é adicionado ao saldo e os observadores são notificados.
     * @param amount O valor a ser depositado.
     */
    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            adjustBalanceAndNotify(amount, "deposit");
            System.out.println("Deposit of " + amount + " to account " + accountNumber + ". New balance: " + balance);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    /**
     * Método abstrato para obter o tipo específico da conta.
     * @return Uma string representando o tipo da conta (ex: "Checking Account", "Savings Account").
     */
    public abstract String getAccountType();
}
