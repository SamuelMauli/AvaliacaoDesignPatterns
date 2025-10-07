package com.bank.account;

/**
 * Interface {@code InterestBearing} define o contrato para contas que podem calcular juros.
 * Esta interface também segue o **Princípio da Segregação de Interfaces (ISP)**, isolando
 * a responsabilidade de cálculo de juros em uma interface específica.
 */
public interface InterestBearing {
    /**
     * Calcula e aplica os juros à conta.
     */
    void calculateInterest();
}
