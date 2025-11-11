package com.bank.command;

import com.bank.account.Withdrawable;
import com.bank.account.Account;
import com.bank.logger.TransactionLogger;

/**
 * Classe {@code WithdrawCommand} implementa a interface {@code Command} para encapsular
 * a operação de saque de uma conta bancária.
 *
 * <p>Parte do padrão **Command**, esta classe transforma uma solicitação de saque
 * em um objeto independente. Isso permite que a operação seja tratada de forma flexível,
 * como ser enfileirada, registrada ou ter sua execução adiada. O logger de transações
 * (Singleton) é utilizado para registrar o evento.
 *
 * <p>Contribui para o **Princípio da Responsabilidade Única (SRP)**, pois esta classe
 * é responsável apenas por executar e registrar a operação de saque, e para o
 * **Princípio da Inversão de Dependência (DIP)**, pois depende da abstração {@code Withdrawable}
 * e da instância do {@code TransactionLogger} (que é um Singleton).
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Encapsulamento:</b> O comando armazena a conta de origem (como Withdrawable
 *         e Account), e o valor a ser sacado. Transforma a operação de saque em um objeto
 *         que pode ser manipulado.</li>
 *     <li><b>Execução:</b> Quando execute() é chamado:
 *         <ul>
 *             <li>Chama account.withdraw(amount) que subtrai o valor do saldo
 *                 (respeitando limites de cheque especial se aplicável) e notifica observadores</li>
 *             <li>Registra a transação no TransactionLogger (Singleton) para histórico</li>
 *         </ul>
 *     </li>
 *     <li><b>Duas Referências:</b> Mantém tanto Withdrawable (para chamar withdraw()) quanto
 *         Account (para obter número da conta e saldo para o log).</li>
 * </ol>
 */
public class WithdrawCommand implements Command {
    /** Conta bancária (como Withdrawable) de onde o saque será realizado.
     *  Usada para chamar o método withdraw(). */
    private Withdrawable account;
    
    /** Referência à conta base (Account) para obter o número da conta e saldo atual.
     *  Usada para registrar detalhes da transação no log. */
    private Account baseAccount;
    
    /** Valor a ser sacado da conta */
    private double amount;
    
    /** Logger de transações (Singleton) usado para registrar o saque no histórico */
    private TransactionLogger logger;

    /**
     * Construtor para criar um comando de saque.
     * <p><b>LÓGICA:</b> Armazena a conta de origem (como Withdrawable e Account), o valor
     * a ser sacado, e obtém a instância única do TransactionLogger (Singleton) para registrar
     * a transação. Mantém duas referências porque:
     * <ul>
     *     <li>Withdrawable: necessário para chamar withdraw()</li>
     *     <li>Account: necessário para obter número da conta e saldo para o log</li>
     * </ul>
     * 
     * @param account A conta bancária (que implementa Withdrawable) de onde o saque será realizado.
     * @param baseAccount A instância da conta base (Account) para acesso a informações como número e saldo.
     * @param amount O valor a ser sacado (deve ser positivo).
     */
    public WithdrawCommand(Withdrawable account, Account baseAccount, double amount) {
        // Armazena a conta como Withdrawable (para chamar withdraw())
        this.account = account;
        // Armazena a conta como Account (para obter número e saldo para o log)
        this.baseAccount = baseAccount;
        // Armazena o valor a ser sacado
        this.amount = amount;
        // Obtém a instância única do TransactionLogger (Singleton)
        // Isso garante que todas as transações sejam registradas no mesmo arquivo de log
        this.logger = TransactionLogger.getInstance();
    }

    /**
     * Executa a operação de saque da conta e registra a transação.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Chama account.withdraw(amount) que:
     *         <ul>
     *             <li>Valida se o valor é positivo</li>
     *             <li>Verifica se há fundos suficientes (considerando limite de cheque especial
     *                 se for conta corrente)</li>
     *             <li>Subtrai o valor do saldo (balance -= amount) - pode deixar saldo negativo
     *                 se for conta corrente com cheque especial</li>
     *             <li>Notifica todos os observadores registrados sobre o saque</li>
     *         </ul>
     *     </li>
     *     <li>Registra a transação no TransactionLogger com os detalhes:
     *         número da conta, valor sacado e novo saldo</li>
     * </ol>
     */
    @Override
    public void execute() {
        // Chama o método withdraw() da conta que:
        // 1. Valida se o valor é positivo
        // 2. Verifica se há fundos suficientes (saldo + limite de cheque especial se aplicável)
        // 3. Subtrai o valor do saldo (balance -= amount)
        // 4. Notifica todos os observadores sobre o evento "withdraw"
        account.withdraw(amount);
        
        // Registra a transação no TransactionLogger (Singleton)
        // O log inclui: número da conta, valor sacado e novo saldo após o saque
        // Usa baseAccount para obter número da conta e saldo atual
        logger.log("Withdrawal: Account " + baseAccount.getAccountNumber() + ", Amount: " + amount + ", New Balance: " + baseAccount.getBalance());
    }
}
