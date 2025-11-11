package com.bank.command;

import com.bank.account.Account;
import com.bank.logger.TransactionLogger;

/**
 * Classe {@code DepositCommand} implementa a interface {@code Command} para encapsular
 * a operação de depósito em uma conta bancária.
 *
 * <p>Parte do padrão **Command**, esta classe transforma uma solicitação de depósito
 * em um objeto independente. Isso permite que a operação seja tratada de forma flexível,
 * como ser enfileirada, registrada ou ter sua execução adiada. O logger de transações
 * (Singleton) é utilizado para registrar o evento.
 *
 * <p>Contribui para o **Princípio da Responsabilidade Única (SRP)**, pois esta classe
 * é responsável apenas por executar e registrar a operação de depósito, e para o
 * **Princípio da Inversão de Dependência (DIP)**, pois depende da abstração {@code Account}
 * e da instância do {@code TransactionLogger} (que é um Singleton).
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Encapsulamento:</b> O comando armazena a conta de destino e o valor a ser depositado.
 *         Transforma a operação de depósito em um objeto que pode ser manipulado (enfileirado,
 *         desfeito, registrado, etc.).</li>
 *     <li><b>Execução:</b> Quando execute() é chamado:
 *         <ul>
 *             <li>Chama account.deposit(amount) que adiciona o valor ao saldo e notifica observadores</li>
 *             <li>Registra a transação no TransactionLogger (Singleton) para histórico</li>
 *         </ul>
 *     </li>
 *     <li><b>Flexibilidade:</b> O padrão Command permite que comandos sejam enfileirados,
 *         desfeitos, registrados, ou executados de forma assíncrona sem modificar o código cliente.</li>
 * </ol>
 */
public class DepositCommand implements Command {
    /** Conta bancária onde o depósito será realizado */
    private Account account;
    
    /** Valor a ser depositado na conta */
    private double amount;
    
    /** Logger de transações (Singleton) usado para registrar o depósito no histórico */
    private TransactionLogger logger;

    /**
     * Construtor para criar um comando de depósito.
     * <p><b>LÓGICA:</b> Armazena a conta de destino e o valor a ser depositado, e obtém
     * a instância única do TransactionLogger (Singleton) para registrar a transação.
     * 
     * @param account A conta bancária onde o depósito será realizado.
     * @param amount O valor a ser depositado (deve ser positivo).
     */
    public DepositCommand(Account account, double amount) {
        // Armazena a conta de destino
        this.account = account;
        // Armazena o valor a ser depositado
        this.amount = amount;
        // Obtém a instância única do TransactionLogger (Singleton)
        // Isso garante que todas as transações sejam registradas no mesmo arquivo de log
        this.logger = TransactionLogger.getInstance();
    }

    /**
     * Executa a operação de depósito na conta e registra a transação.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Chama account.deposit(amount) que:
     *         <ul>
     *             <li>Valida se o valor é positivo</li>
     *             <li>Adiciona o valor ao saldo (balance += amount)</li>
     *             <li>Notifica todos os observadores registrados sobre o depósito</li>
     *         </ul>
     *     </li>
     *     <li>Registra a transação no TransactionLogger com os detalhes:
     *         número da conta, valor depositado e novo saldo</li>
     * </ol>
     */
    @Override
    public void execute() {
        // Chama o método deposit() da conta que:
        // 1. Valida se o valor é positivo
        // 2. Adiciona o valor ao saldo (balance += amount)
        // 3. Notifica todos os observadores sobre o evento "deposit"
        account.deposit(amount);
        
        // Registra a transação no TransactionLogger (Singleton)
        // O log inclui: número da conta, valor depositado e novo saldo após o depósito
        logger.log("Deposit: Account " + account.getAccountNumber() + ", Amount: " + amount + ", New Balance: " + account.getBalance());
    }
}
