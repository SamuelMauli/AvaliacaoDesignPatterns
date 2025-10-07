package com.bank.strategy;

/**
 * Interface {@code InterestCalculationStrategy} define o contrato para diferentes
 * algoritmos de cálculo de juros. Este é o componente Strategy do padrão de projeto **Strategy**.
 *
 * <p>O padrão Strategy permite que o comportamento de um objeto seja selecionado em tempo de execução.
 * No contexto bancário, diferentes tipos de contas poupança podem ter diferentes formas de calcular
 * juros (juros simples, juros compostos, juros de alto rendimento, etc.). Esta interface permite
 * que esses algoritmos sejam encapsulados em classes separadas e intercambiáveis.
 *
 * <p>Este padrão contribui para o **Princípio Aberto/Fechado (OCP)**, pois novas estratégias de
 * cálculo de juros podem ser adicionadas sem modificar a classe {@code SavingsAccount}.
 * Também adere ao **Princípio da Inversão de Dependência (DIP)**, pois a {@code SavingsAccount}
 * depende da abstração {@code InterestCalculationStrategy} e não de implementações concretas.
 */
public interface InterestCalculationStrategy {
    /**
     * Calcula os juros com base no saldo e na taxa de juros fornecidos.
     * @param balance O saldo atual da conta.
     * @param interestRate A taxa de juros a ser aplicada.
     * @return O valor dos juros calculados.
     */
    double calculateInterest(double balance, double interestRate);
}
