package com.bank.account;

import com.bank.strategy.InterestCalculationStrategy;
import com.bank.strategy.SimpleInterestStrategy;

/**
 * Classe {@code SavingsAccount} representa uma conta poupança, estendendo a funcionalidade
 * básica de {@code Account} e implementando as interfaces {@code Withdrawable} e {@code InterestBearing}.
 * Contas poupança calculam juros e não possuem limite de cheque especial.
 *
 * <p>Esta classe demonstra a aplicação dos seguintes princípios SOLID e padrões de projeto:
 * <ul>
 *     <li>**Princípio da Substituição de Liskov (LSP)**: {@code SavingsAccount} pode ser
 *         substituída por {@code Account}, {@code Withdrawable} ou {@code InterestBearing}
 *         sem quebrar a funcionalidade do programa. O método {@code withdraw} é implementado
 *         com uma lógica de saldo suficiente, e {@code calculateInterest} aplica a estratégia
 *         de juros.</li>
 *     <li>**Princípio Aberto/Fechado (OCP)**: A lógica de cálculo de juros é delegada a uma
 *         estratégia (padrão Strategy), permitindo a adição de novas formas de cálculo de juros
 *         sem modificar esta classe.</li>
 *     <li>**Princípio da Inversão de Dependência (DIP)**: A classe depende da abstração
 *         {@code InterestCalculationStrategy} em vez de uma implementação concreta, permitindo
 *         flexibilidade na escolha da estratégia de juros.</li>
 *     <li>**Padrão Strategy**: O comportamento de cálculo de juros é encapsulado em objetos
 *         de estratégia, que podem ser trocados em tempo de execução.</li>
 * </ul>
 */
public class SavingsAccount extends Account implements Withdrawable, InterestBearing {
    private InterestCalculationStrategy interestStrategy;
    private double interestRate; // Ainda necessário para a estratégia utilizar

    /**
     * Construtor para criar uma nova conta poupança.
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     * @param interestRate A taxa de juros anual para esta conta.
     */
    public SavingsAccount(String customerName, double initialBalance, double interestRate) {
        super(customerName, initialBalance);
        this.interestRate = interestRate;
        this.interestStrategy = new SimpleInterestStrategy(); // Estratégia padrão
    }

    /**
     * Define a estratégia de cálculo de juros para esta conta.
     * @param interestStrategy A nova estratégia de cálculo de juros.
     */
    public void setInterestStrategy(InterestCalculationStrategy interestStrategy) {
        this.interestStrategy = interestStrategy;
    }

    /**
     * Realiza um saque da conta poupança. Requer saldo suficiente, sem cheque especial.
     * @param amount O valor a ser sacado. Deve ser um valor positivo.
     */
    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            if (this.balance >= amount) {
                adjustBalanceAndNotify(-amount, "withdraw");
                System.out.println("Withdrawal of " + amount + " from account " + accountNumber + ". New balance: " + balance);
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    /**
     * Calcula e aplica os juros à conta usando a estratégia de juros configurada.
     */
    @Override
    public void calculateInterest() {
        double interest = interestStrategy.calculateInterest(this.balance, this.interestRate);
        adjustBalanceAndNotify(interest, "interest_calculation");
        System.out.println("Interest of " + interest + " added to account " + accountNumber + ". New balance: " + balance);
    }

    /**
     * Retorna o tipo da conta.
     * @return Uma string "Savings Account".
     */
    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    /**
     * Retorna a taxa de juros atual da conta.
     * @return A taxa de juros.
     */
    public double getInterestRate() {
        return interestRate;
    }
}
