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
 */
public class WithdrawCommand implements Command {
    private Withdrawable account;
    private Account baseAccount; // Referência à conta base para obter o número e saldo atual
    private double amount;
    private TransactionLogger logger;

    /**
     * Construtor para criar um comando de saque.
     * @param account A conta bancária (que implementa Withdrawable) de onde o saque será realizado.
     * @param baseAccount A instância da conta base (Account) para acesso a informações como número e saldo.
     * @param amount O valor a ser sacado.
     */
    public WithdrawCommand(Withdrawable account, Account baseAccount, double amount) {
        this.account = account;
        this.baseAccount = baseAccount;
        this.amount = amount;
        this.logger = TransactionLogger.getInstance();
    }

    /**
     * Executa a operação de saque da conta e registra a transação.
     */
    @Override
    public void execute() {
        account.withdraw(amount);
        logger.log("Withdrawal: Account " + baseAccount.getAccountNumber() + ", Amount: " + amount + ", New Balance: " + baseAccount.getBalance());
    }
}
