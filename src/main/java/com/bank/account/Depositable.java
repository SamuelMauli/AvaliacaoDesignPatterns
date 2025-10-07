package com.bank.account;

/**
 * Interface {@code Depositable} define o contrato para operações de depósito em uma conta bancária.
 * Este é um exemplo de aplicação do **Princípio da Segregação de Interfaces (ISP)**, garantindo
 * que as classes que implementam esta interface sejam responsáveis apenas pela funcionalidade
 * de depósito, sem serem forçadas a depender de métodos que não utilizam.
 */
public interface Depositable {
    /**
     * Realiza um depósito na conta.
     * @param amount O valor a ser depositado. Deve ser um valor positivo.
     */
    void deposit(double amount);
}
