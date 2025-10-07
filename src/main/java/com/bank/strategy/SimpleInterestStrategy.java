package com.bank.strategy;

/**
 * Classe {@code SimpleInterestStrategy} implementa a interface {@code InterestCalculationStrategy},
 * fornecendo uma estratégia concreta para o cálculo de juros simples.
 *
 * <p>Esta classe é uma implementação do padrão **Strategy**, onde o algoritmo de cálculo de juros
 * é encapsulado e pode ser trocado dinamicamente. A lógica aqui é simples: juros são calculados
 * como uma porcentagem direta do saldo atual.
 *
 * <p>Contribui para o **Princípio da Responsabilidade Única (SRP)**, pois seu único propósito
 * é calcular juros simples, e para o **Princípio Aberto/Fechado (OCP)**, pois novas estratégias
 * de juros podem ser adicionadas sem modificar esta classe ou a {@code SavingsAccount}.
 */
public class SimpleInterestStrategy implements InterestCalculationStrategy {
    /**
     * Calcula os juros simples com base no saldo e na taxa de juros.
     * @param balance O saldo atual da conta.
     * @param interestRate A taxa de juros a ser aplicada.
     * @return O valor dos juros calculados.
     */
    @Override
    public double calculateInterest(double balance, double interestRate) {
        return balance * interestRate;
    }
}
