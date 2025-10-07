package com.bank.facade;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.command.Command;
import com.bank.command.DepositCommand;
import com.bank.command.WithdrawCommand;
import com.bank.factory.AccountFactory;
import com.bank.account.Withdrawable;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe {@code BankingFacade} implementa o padrão de projeto **Facade**.
 * Ela fornece uma interface simplificada e de alto nível para os subsistemas
 * internos complexos do banco, como criação de contas, depósitos e saques.
 *
 * <p>A principal vantagem do padrão Facade é reduzir a complexidade e o acoplamento
 * entre o cliente e os subsistemas. O cliente interage apenas com a Facade, que por sua vez,
 * coordena as operações com as classes e padrões subjacentes (Factory, Command, etc.).
 *
 * <p>Este padrão contribui para o **Princípio da Inversão de Dependência (DIP)**,
 * pois o cliente depende da abstração fornecida pela Facade e não das implementações
 * detalhadas dos subsistemas. Também melhora a **coesão** e reduz o **acoplamento**.
 */
public class BankingFacade {
    private Map<String, Account> accounts;

    /**
     * Construtor para {@code BankingFacade}. Inicializa o mapa de contas.
     */
    public BankingFacade() {
        this.accounts = new HashMap<>();
    }

    /**
     * Cria uma nova conta bancária usando o {@code AccountFactory} e a registra na Facade.
     * @param type O tipo de conta a ser criada (CHECKING ou SAVINGS).
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     * @param params Parâmetros adicionais específicos do tipo de conta.
     * @return O número da conta recém-criada.
     */
    public String createAccount(AccountType type, String customerName, double initialBalance, double... params) {
        Account account = AccountFactory.createAccount(type, customerName, initialBalance, params);
        accounts.put(account.getAccountNumber(), account);
        System.out.println("Account created: " + account.getAccountType() + " for " + customerName + " with account number " + account.getAccountNumber());
        return account.getAccountNumber();
    }

    /**
     * Retorna uma conta com base no seu número.
     * @param accountNumber O número da conta.
     * @return A instância da {@code Account} ou null se não encontrada.
     */
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    /**
     * Realiza um depósito em uma conta específica usando o padrão Command.
     * @param accountNumber O número da conta de destino.
     * @param amount O valor a ser depositado.
     */
    public void deposit(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            Command deposit = new DepositCommand(account, amount);
            deposit.execute();
        } else {
            System.out.println("Account not found: " + accountNumber);
        }
    }

    /**
     * Realiza um saque de uma conta específica usando o padrão Command.
     * Verifica se a conta suporta saques (implementa {@code Withdrawable}).
     * @param accountNumber O número da conta de origem.
     * @param amount O valor a ser sacado.
     */
    public void withdraw(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account instanceof Withdrawable) {
            Command withdraw = new WithdrawCommand((Withdrawable) account, account, amount);
            withdraw.execute();
        } else if (account != null) {
            System.out.println("Withdrawal not supported for this account type: " + account.getAccountType());
        } else {
            System.out.println("Account not found: " + accountNumber);
        }
    }

    /**
     * Retorna o saldo de uma conta específica.
     * @param accountNumber O número da conta.
     * @return O saldo da conta, ou -1.0 se a conta não for encontrada.
     */
    public double getBalance(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            return account.getBalance();
        } else {
            System.out.println("Account not found: " + accountNumber);
            return -1.0; // Indica erro ou conta não encontrada
        }
    }
}
