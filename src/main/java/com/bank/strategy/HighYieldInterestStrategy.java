package com.bank.strategy;

/**
 * Classe {@code HighYieldInterestStrategy} implementa a interface {@code InterestCalculationStrategy},
 * fornecendo uma estratégia concreta para o cálculo de juros de alto rendimento.
 *
 * <p>Esta classe é uma implementação do padrão **Strategy**, oferecendo uma alternativa
 * ao cálculo de juros simples. A lógica aqui é um exemplo de como uma estratégia de
 * alto rendimento pode ser implementada, adicionando um bônus à taxa de juros base.
 *
 * <p>Contribui para o **Princípio da Responsabilidade Única (SRP)**, pois seu único propósito
 * é calcular juros de alto rendimento, e para o **Princípio Aberto/Fechado (OCP)**, pois
 * permite que novas estratégias de juros sejam adicionadas sem modificar a classe
 * {@code SavingsAccount} ou outras estratégias existentes.
 */
public class HighYieldInterestStrategy implements InterestCalculationStrategy {
    /**
     * Calcula os juros de alto rendimento com base no saldo e na taxa de juros.
     * Adiciona um bônus de 1% à taxa de juros base.
     * @param balance O saldo atual da conta.
     * @param interestRate A taxa de juros base a ser aplicada.
     * @return O valor dos juros calculados com o bônus.
     */
    @Override
    public double calculateInterest(double balance, double interestRate) {
        // Exemplo: Alto rendimento pode ser uma taxa ligeiramente maior ou um bônus
        return balance * (interestRate + 0.01); // 1% de juros bônus
    }
}
