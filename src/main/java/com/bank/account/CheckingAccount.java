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
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Criação:</b> Uma conta corrente é criada com um nome de cliente, saldo inicial
 *         e um limite de cheque especial. O limite permite que a conta tenha saldo negativo
 *         até esse valor.</li>
 *     <li><b>Saque:</b> O método withdraw() permite saques que excedam o saldo atual, desde que
 *         o valor total (saldo + limite de cheque especial) seja suficiente. Por exemplo, se o
 *         saldo é R$ 100 e o limite é R$ 500, é possível sacar até R$ 600.</li>
 *     <li><b>Validação:</b> Antes de realizar o saque, verifica se (saldo + limite) >= valor do saque.
 *         Se sim, realiza o saque e notifica observadores. Se não, rejeita a operação.</li>
 * </ol>
 */
public class CheckingAccount extends Account implements Withdrawable {
    /** Limite de cheque especial permitido para esta conta corrente.
     *  Permite que a conta tenha saldo negativo até este valor.
     *  Exemplo: se overdraftLimit = 500, a conta pode ter saldo de até -500. */
    private double overdraftLimit;

    /**
     * Construtor para criar uma nova conta corrente.
     * <p><b>LÓGICA:</b> Chama o construtor da classe pai (Account) para inicializar os campos
     * básicos (número da conta, nome do cliente, saldo inicial) e define o limite de cheque especial.
     * 
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     * @param overdraftLimit O limite do cheque especial permitido para esta conta (ex: 500.0).
     */
    public CheckingAccount(String customerName, double initialBalance, double overdraftLimit) {
        // Chama o construtor da classe pai para inicializar: accountNumber, customerName, balance
        super(customerName, initialBalance);
        // Define o limite de cheque especial específico desta conta corrente
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Realiza um saque da conta corrente. Permite saques que excedam o saldo atual
     * até o limite do cheque especial.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Valida se o valor do saque é positivo</li>
     *     <li>Verifica se há fundos suficientes: (saldo atual + limite de cheque especial) >= valor do saque</li>
     *     <li>Se houver fundos suficientes:
     *         <ul>
     *             <li>Chama adjustBalanceAndNotify(-amount, "withdraw") que:
     *                 <ul>
     *                     <li>Subtrai o valor do saldo (balance -= amount)</li>
     *                     <li>Notifica todos os observadores sobre o saque</li>
     *                 </ul>
     *             </li>
     *             <li>Imprime mensagem de confirmação</li>
     *         </ul>
     *     </li>
     *     <li>Se não houver fundos suficientes, rejeita a operação e exibe mensagem de erro</li>
     * </ol>
     * 
     * @param amount O valor a ser sacado (deve ser positivo).
     */
    @Override
    public void withdraw(double amount) {
        // Valida se o valor do saque é positivo
        if (amount > 0) {
            // Verifica se há fundos suficientes considerando o saldo atual + limite de cheque especial
            // Exemplo: se balance = 100 e overdraftLimit = 500, pode sacar até 600
            if (this.balance + this.overdraftLimit >= amount) {
                // Chama adjustBalanceAndNotify que:
                // 1. Subtrai o valor do saldo (balance -= amount) - pode deixar o saldo negativo
                // 2. Notifica todos os observadores sobre o evento "withdraw"
                adjustBalanceAndNotify(-amount, "withdraw");
                // Imprime mensagem de confirmação no console
                System.out.println("Withdrawal of " + amount + " from account " + accountNumber + ". New balance: " + balance);
            } else {
                // Se não houver fundos suficientes (saldo + limite < valor do saque), rejeita
                System.out.println("Insufficient funds and overdraft limit exceeded.");
            }
        } else {
            // Se o valor não for positivo, exibe mensagem de erro
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
