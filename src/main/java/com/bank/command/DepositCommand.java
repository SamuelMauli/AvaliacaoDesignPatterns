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
 */
public class DepositCommand implements Command {
    private Account account;
    private double amount;
    private TransactionLogger logger;

    /**
     * Construtor para criar um comando de depósito.
     * @param account A conta bancária onde o depósito será realizado.
     * @param amount O valor a ser depositado.
     */
    public DepositCommand(Account account, double amount) {
        this.account = account;
        this.amount = amount;
        this.logger = TransactionLogger.getInstance();
    }

    /**
     * Executa a operação de depósito na conta e registra a transação.
     */
    @Override
    public void execute() {
        account.deposit(amount);
        logger.log("Deposit: Account " + account.getAccountNumber() + ", Amount: " + amount + ", New Balance: " + account.getBalance());
    }
}
