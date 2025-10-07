package com.bank.account;

/**
 * Classe {@code CheckingAccount} representa uma conta corrente, estendendo a funcionalidade
 * básica de {@code Account} e implementando a interface {@code Withdrawable}.
 * Contas correntes possuem um limite de cheque especial (overdraft limit).
 *
 * <p>Esta classe demonstra a aplicação dos seguintes princípios SOLID:
 * <ul>
 *     <li>**Princípio da Substituição de Liskov (LSP)**: {@code CheckingAccount} pode ser
 *         substituída por {@code Account} ou {@code Withdrawable} sem quebrar a funcionalidade
 *         do programa. O método {@code withdraw} é implementado de forma a respeitar o contrato
 *         da interface {@code Withdrawable}, adicionando a lógica específica de cheque especial.</li>
 *     <li>**Princípio Aberto/Fechado (OCP)**: A lógica de saque com cheque especial é encapsulada
 *         aqui. Se um novo tipo de conta com diferentes regras de saque for necessário, uma nova
 *         subclasse pode ser criada sem modificar esta classe.</li>
 * </ul>
 */
public class CheckingAccount extends Account implements Withdrawable {
    private double overdraftLimit;

    /**
     * Construtor para criar uma nova conta corrente.
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     * @param overdraftLimit O limite do cheque especial permitido para esta conta.
     */
    public CheckingAccount(String customerName, double initialBalance, double overdraftLimit) {
        super(customerName, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Realiza um saque da conta corrente. Permite saques que excedam o saldo atual
     * até o limite do cheque especial.
     * @param amount O valor a ser sacado. Deve ser um valor positivo.
     */
    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            if (this.balance + this.overdraftLimit >= amount) {
                adjustBalanceAndNotify(-amount, "withdraw");
                System.out.println("Withdrawal of " + amount + " from account " + accountNumber + ". New balance: " + balance);
            } else {
                System.out.println("Insufficient funds and overdraft limit exceeded.");
            }
        } else {
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    /**
     * Retorna o tipo da conta.
     * @return Uma string "Checking Account".
     */
    @Override
    public String getAccountType() {
        return "Checking Account";
    }

    /**
     * Retorna o limite do cheque especial da conta.
     * @return O limite do cheque especial.
     */
    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}
