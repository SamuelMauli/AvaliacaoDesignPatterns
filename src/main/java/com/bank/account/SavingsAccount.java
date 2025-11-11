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
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Criação:</b> Uma conta poupança é criada com nome do cliente, saldo inicial e taxa de juros.
 *         Por padrão, usa SimpleInterestStrategy para calcular juros.</li>
 *     <li><b>Saque:</b> Diferente da conta corrente, só permite saques se houver saldo suficiente
 *         (não permite saldo negativo). Verifica se balance >= amount antes de realizar o saque.</li>
 *     <li><b>Cálculo de Juros:</b> Usa o padrão Strategy - a estratégia de juros pode ser trocada
 *         dinamicamente. Chama interestStrategy.calculateInterest() passando o saldo e a taxa de juros,
 *         depois adiciona o valor calculado ao saldo e notifica observadores.</li>
 *     <li><b>Troca de Estratégia:</b> É possível trocar a estratégia de juros em tempo de execução
 *         usando setInterestStrategy(), permitindo mudar de juros simples para alto rendimento, etc.</li>
 * </ol>
 */
public class SavingsAccount extends Account implements Withdrawable, InterestBearing {
    /** Estratégia de cálculo de juros utilizada por esta conta poupança.
     *  Implementa o padrão Strategy - permite trocar o algoritmo de cálculo de juros
     *  dinamicamente sem modificar esta classe. */
    private InterestCalculationStrategy interestStrategy;
    
    /** Taxa de juros anual desta conta poupança (ex: 0.05 = 5% ao ano).
     *  É passada para a estratégia de juros quando calculateInterest() é chamado. */
    private double interestRate;

    /**
     * Construtor para criar uma nova conta poupança.
     * <p><b>LÓGICA:</b> Inicializa os campos básicos através do construtor da classe pai,
     * define a taxa de juros e configura a estratégia padrão de cálculo de juros (SimpleInterestStrategy).
     * 
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     * @param interestRate A taxa de juros anual para esta conta (ex: 0.05 = 5% ao ano).
     */
    public SavingsAccount(String customerName, double initialBalance, double interestRate) {
        // Chama o construtor da classe pai para inicializar: accountNumber, customerName, balance
        super(customerName, initialBalance);
        // Define a taxa de juros desta conta poupança
        this.interestRate = interestRate;
        // Configura a estratégia padrão de cálculo de juros (juros simples)
        // Esta estratégia pode ser trocada posteriormente usando setInterestStrategy()
        this.interestStrategy = new SimpleInterestStrategy();
    }

    /**
     * Define a estratégia de cálculo de juros para esta conta.
     * <p><b>LÓGICA:</b> Permite trocar a estratégia de cálculo de juros em tempo de execução.
     * Isso demonstra o padrão Strategy - o comportamento de cálculo de juros é encapsulado
     * em objetos de estratégia que podem ser trocados dinamicamente.
     * 
     * @param interestStrategy A nova estratégia de cálculo de juros (ex: HighYieldInterestStrategy).
     */
    public void setInterestStrategy(InterestCalculationStrategy interestStrategy) {
        // Troca a estratégia de cálculo de juros - permite mudar o algoritmo sem modificar esta classe
        this.interestStrategy = interestStrategy;
    }

    /**
     * Realiza um saque da conta poupança. Requer saldo suficiente, sem cheque especial.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Valida se o valor do saque é positivo</li>
     *     <li>Verifica se há saldo suficiente (balance >= amount) - diferente da conta corrente,
     *         não permite saldo negativo</li>
     *     <li>Se houver saldo suficiente:
     *         <ul>
     *             <li>Chama adjustBalanceAndNotify(-amount, "withdraw") que:
     *                 <ul>
     *                     <li>Subtrai o valor do saldo</li>
     *                     <li>Notifica todos os observadores</li>
     *                 </ul>
     *             </li>
     *             <li>Imprime mensagem de confirmação</li>
     *         </ul>
     *     </li>
     *     <li>Se não houver saldo suficiente, rejeita a operação</li>
     * </ol>
     * 
     * @param amount O valor a ser sacado (deve ser positivo).
     */
    @Override
    public void withdraw(double amount) {
        // Valida se o valor do saque é positivo
        if (amount > 0) {
            // Verifica se há saldo suficiente - conta poupança não permite saldo negativo
            // Diferente da conta corrente, não considera limite de cheque especial
            if (this.balance >= amount) {
                // Chama adjustBalanceAndNotify que:
                // 1. Subtrai o valor do saldo (balance -= amount)
                // 2. Notifica todos os observadores sobre o evento "withdraw"
                adjustBalanceAndNotify(-amount, "withdraw");
                // Imprime mensagem de confirmação no console
                System.out.println("Withdrawal of " + amount + " from account " + accountNumber + ". New balance: " + balance);
            } else {
                // Se não houver saldo suficiente, rejeita a operação
                System.out.println("Insufficient funds.");
            }
        } else {
            // Se o valor não for positivo, exibe mensagem de erro
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    /**
     * Calcula e aplica os juros à conta usando a estratégia de juros configurada.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Chama a estratégia de juros configurada (interestStrategy.calculateInterest())
     *         passando o saldo atual e a taxa de juros</li>
     *     <li>A estratégia retorna o valor dos juros calculados</li>
     *     <li>Chama adjustBalanceAndNotify(interest, "interest_calculation") que:
     *         <ul>
     *             <li>Adiciona o valor dos juros ao saldo (balance += interest)</li>
     *             <li>Notifica todos os observadores sobre o cálculo de juros</li>
     *         </ul>
     *     </li>
     *     <li>Imprime mensagem de confirmação</li>
     * </ol>
     * <p>O padrão Strategy permite que diferentes algoritmos de cálculo de juros sejam usados
     * (juros simples, juros compostos, alto rendimento, etc.) sem modificar esta classe.
     */
    @Override
    public void calculateInterest() {
        // Chama a estratégia de juros configurada para calcular os juros
        // A estratégia recebe o saldo atual e a taxa de juros, retorna o valor dos juros
        double interest = interestStrategy.calculateInterest(this.balance, this.interestRate);
        
        // Chama adjustBalanceAndNotify que:
        // 1. Adiciona o valor dos juros ao saldo (balance += interest)
        // 2. Notifica todos os observadores sobre o evento "interest_calculation"
        adjustBalanceAndNotify(interest, "interest_calculation");
        
        // Imprime mensagem de confirmação no console
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
