package com.bank.factory;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.CheckingAccount;
import com.bank.account.SavingsAccount;

/**
 * Classe {@code AccountFactory} implementa o padrão de projeto **Factory Method**.
 * Ela é responsável por criar instâncias de diferentes tipos de contas bancárias
 * ({@code CheckingAccount}, {@code SavingsAccount}) com base no {@code AccountType} fornecido,
 * sem expor a lógica de instanciação ao código cliente.
 *
 * <p>Este padrão promove o **Princípio Aberto/Fechado (OCP)**, pois novos tipos de conta
 * podem ser adicionados ao sistema modificando apenas esta fábrica, sem a necessidade
 * de alterar o código cliente que solicita a criação de contas. Também contribui para
 * o **Princípio da Inversão de Dependência (DIP)**, pois o cliente depende da abstração
 * {@code Account} e da fábrica, e não das classes concretas de conta.
 */
public class AccountFactory {
    /**
     * Cria uma nova instância de {@code Account} com base no tipo especificado.
     * @param type O tipo de conta a ser criada (CHECKING ou SAVINGS).
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     * @param params Parâmetros adicionais específicos do tipo de conta (ex: limite de cheque especial, taxa de juros).
     * @return Uma nova instância de {@code Account} do tipo especificado.
     * @throws IllegalArgumentException Se o tipo de conta for desconhecido ou nulo.
     */
    public static Account createAccount(AccountType type, String customerName, double initialBalance, double... params) {
        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null.");
        }
        switch (type) {
            case CHECKING:
                // Para CheckingAccount, params[0] é o limite de cheque especial
                return new CheckingAccount(customerName, initialBalance, params.length > 0 ? params[0] : 0.0);
            case SAVINGS:
                // Para SavingsAccount, params[0] é a taxa de juros
                return new SavingsAccount(customerName, initialBalance, params.length > 0 ? params[0] : 0.0);
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
}
