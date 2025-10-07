package com.bank.account;

/**
 * Interface {@code Withdrawable} define o contrato para operações de saque em uma conta bancária.
 * Assim como {@code Depositable}, esta interface adere ao **Princípio da Segregação de Interfaces (ISP)**,
 * permitindo que apenas as classes que realmente suportam saques implementem este método.
 */
public interface Withdrawable {
    /**
     * Realiza um saque da conta.
     * @param amount O valor a ser sacado. Deve ser um valor positivo.
     */
    void withdraw(double amount);
}
