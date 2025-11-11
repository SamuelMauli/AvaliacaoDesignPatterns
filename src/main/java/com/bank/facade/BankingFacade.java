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
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Armazenamento:</b> Mantém um mapa (HashMap) de todas as contas do sistema,
 *         usando o número da conta como chave e o objeto Account como valor.</li>
 *     <li><b>Criação de Contas:</b> Usa AccountFactory (Factory Method) para criar contas
 *         sem expor a lógica de instanciação ao cliente. Registra a conta criada no mapa.</li>
 *     <li><b>Depósitos e Saques:</b> Usa o padrão Command - cria objetos DepositCommand
 *         ou WithdrawCommand que encapsulam a operação e a executam. Isso permite
 *         flexibilidade (ex: enfileirar comandos, desfazer operações, etc.).</li>
 *     <li><b>Transferências:</b> Combina um saque da conta de origem e um depósito
 *         na conta de destino, ambos usando o padrão Command. Valida saldo suficiente
 *         antes de executar.</li>
 *     <li><b>Histórico:</b> Obtém o histórico de transações do TransactionLogger (Singleton),
 *         que registra todas as operações realizadas no sistema.</li>
 * </ol>
 */
public class BankingFacade {
    /** Mapa que armazena todas as contas do sistema.
     *  Chave: número da conta (String)
     *  Valor: objeto Account correspondente
     *  Permite acesso rápido a qualquer conta pelo seu número. */
    private Map<String, Account> accounts;

    /**
     * Construtor para {@code BankingFacade}. Inicializa o mapa de contas.
     * <p><b>LÓGICA:</b> Cria um HashMap vazio que será usado para armazenar todas as contas
     * criadas no sistema. O mapa permite acesso rápido a qualquer conta pelo seu número.
     */
    public BankingFacade() {
        // Inicializa o mapa vazio - será populado conforme contas são criadas
        this.accounts = new HashMap<>();
    }

    /**
     * Cria uma nova conta bancária usando o {@code AccountFactory} e a registra na Facade.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Usa AccountFactory.createAccount() (Factory Method) para criar a conta apropriada
     *         baseada no tipo fornecido. A factory esconde a lógica de instanciação do cliente.</li>
     *     <li>Registra a conta criada no mapa interno usando o número da conta como chave.</li>
     *     <li>Retorna o número da conta para que o cliente possa referenciá-la futuramente.</li>
     * </ol>
     * 
     * @param type O tipo de conta a ser criada (CHECKING ou SAVINGS).
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     * @param params Parâmetros adicionais específicos do tipo de conta:
     *               - Para CHECKING: params[0] = limite de cheque especial
     *               - Para SAVINGS: params[0] = taxa de juros
     * @return O número da conta recém-criada.
     */
    public String createAccount(AccountType type, String customerName, double initialBalance, double... params) {
        // Usa o Factory Method para criar a conta apropriada
        // A factory decide qual classe concreta instanciar (CheckingAccount ou SavingsAccount)
        // sem expor essa lógica ao cliente
        Account account = AccountFactory.createAccount(type, customerName, initialBalance, params);
        
        // Registra a conta no mapa interno usando o número da conta como chave
        // Isso permite acesso rápido à conta pelo seu número
        accounts.put(account.getAccountNumber(), account);
        
        // Imprime mensagem de confirmação no console
        System.out.println("Account created: " + account.getAccountType() + " for " + customerName + " with account number " + account.getAccountNumber());
        
        // Retorna o número da conta para que o cliente possa referenciá-la
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
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Busca a conta no mapa usando o número da conta</li>
     *     <li>Se a conta existir, cria um DepositCommand que encapsula a operação de depósito</li>
     *     <li>Executa o comando, que por sua vez:
     *         <ul>
     *             <li>Chama account.deposit(amount) que adiciona o valor ao saldo</li>
     *             <li>Registra a transação no TransactionLogger (Singleton)</li>
     *             <li>Notifica observadores sobre o depósito</li>
     *         </ul>
     *     </li>
     * </ol>
     * O padrão Command permite que a operação seja tratada como um objeto, possibilitando
     * enfileirar comandos, desfazer operações, registrar histórico, etc.
     * 
     * @param accountNumber O número da conta de destino.
     * @param amount O valor a ser depositado.
     */
    public void deposit(String accountNumber, double amount) {
        // Busca a conta no mapa usando o número da conta
        Account account = accounts.get(accountNumber);
        
        if (account != null) {
            // Cria um comando de depósito que encapsula a operação
            // O padrão Command transforma a solicitação em um objeto independente
            Command deposit = new DepositCommand(account, amount);
            
            // Executa o comando, que:
            // 1. Chama account.deposit(amount) - adiciona valor ao saldo e notifica observadores
            // 2. Registra a transação no TransactionLogger
            deposit.execute();
        } else {
            // Se a conta não for encontrada, exibe mensagem de erro
            System.out.println("Account not found: " + accountNumber);
        }
    }

    /**
     * Realiza um saque de uma conta específica usando o padrão Command.
     * Verifica se a conta suporta saques (implementa {@code Withdrawable}).
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Busca a conta no mapa usando o número da conta</li>
     *     <li>Verifica se a conta implementa Withdrawable (suporta saques)</li>
     *     <li>Se suportar, cria um WithdrawCommand que encapsula a operação de saque</li>
     *     <li>Executa o comando, que por sua vez:
     *         <ul>
     *             <li>Chama account.withdraw(amount) que subtrai o valor do saldo
     *                 (respeitando limites de cheque especial se aplicável)</li>
     *             <li>Registra a transação no TransactionLogger</li>
     *             <li>Notifica observadores sobre o saque</li>
     *         </ul>
     *     </li>
     * </ol>
     * 
     * @param accountNumber O número da conta de origem.
     * @param amount O valor a ser sacado.
     */
    public void withdraw(String accountNumber, double amount) {
        // Busca a conta no mapa usando o número da conta
        Account account = accounts.get(accountNumber);
        
        // Verifica se a conta implementa Withdrawable (suporta saques)
        if (account instanceof Withdrawable) {
            // Cria um comando de saque que encapsula a operação
            // Passa tanto a conta como Withdrawable quanto como Account para acesso completo
            Command withdraw = new WithdrawCommand((Withdrawable) account, account, amount);
            
            // Executa o comando, que:
            // 1. Chama account.withdraw(amount) - subtrai valor do saldo (respeitando limites)
            // 2. Registra a transação no TransactionLogger
            // 3. Notifica observadores sobre o saque
            withdraw.execute();
        } else if (account != null) {
            // Se a conta existe mas não suporta saques, exibe mensagem de erro
            System.out.println("Withdrawal not supported for this account type: " + account.getAccountType());
        } else {
            // Se a conta não for encontrada, exibe mensagem de erro
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

    /**
     * Retorna uma lista com todos os números de conta registrados na Facade.
     * Este método foi adicionado para facilitar a integração com a GUI.
     * @return Uma lista contendo todos os números de conta.
     */
    public java.util.List<String> getAllAccountNumbers() {
        return new java.util.ArrayList<>(accounts.keySet());
    }

    /**
     * Retorna uma cópia do mapa de contas para operações de leitura.
     * Este método foi adicionado para facilitar a integração com a GUI,
     * mantendo o encapsulamento ao retornar uma cópia.
     * @return Uma cópia do mapa de contas.
     */
    public Map<String, Account> getAllAccounts() {
        return new HashMap<>(accounts);
    }

    /**
     * Verifica se uma conta existe no sistema.
     * @param accountNumber O número da conta a ser verificada.
     * @return true se a conta existe, false caso contrário.
     */
    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    /**
     * Retorna o número total de contas registradas no sistema.
     * @return O número total de contas.
     */
    public int getTotalAccountsCount() {
        return accounts.size();
    }

    /**
     * Retorna o histórico de transações completo do sistema.
     * Utiliza o {@code TransactionLogger} (Singleton) para obter os logs.
     * @return Uma lista de strings, onde cada string representa uma transação logada.
     */
    public java.util.List<String> getTransactionHistory() {
        return com.bank.logger.TransactionLogger.getInstance().getLogs();
    }

    /**
     * Realiza uma transferência entre duas contas.
     * Esta operação combina um saque da conta de origem e um depósito na conta de destino.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li><b>Validação:</b> Verifica se ambas as contas existem, se a conta de origem
     *         suporta saques, e se o valor é positivo</li>
     *     <li><b>Verificação de Saldo:</b> Verifica se há fundos suficientes na conta de origem:
     *         <ul>
     *             <li>Se for conta corrente: considera saldo + limite de cheque especial</li>
     *             <li>Se for conta poupança: verifica apenas o saldo (não permite negativo)</li>
     *         </ul>
     *     </li>
     *     <li><b>Execução:</b> Se todas as validações passarem:
     *         <ul>
     *             <li>Cria e executa um WithdrawCommand na conta de origem (usa Command Pattern)</li>
     *             <li>Cria e executa um DepositCommand na conta de destino (usa Command Pattern)</li>
     *             <li>Cada comando registra a transação no TransactionLogger e notifica observadores</li>
     *         </ul>
     *     </li>
     * </ol>
     * 
     * @param fromAccountNumber Número da conta de origem.
     * @param toAccountNumber Número da conta de destino.
     * @param amount Valor a ser transferido (deve ser positivo).
     * @return true se a transferência foi bem-sucedida, false caso contrário.
     */
    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        // Busca ambas as contas no mapa
        Account fromAccount = accounts.get(fromAccountNumber);
        Account toAccount = accounts.get(toAccountNumber);

        // Valida se a conta de origem existe
        if (fromAccount == null) {
            System.out.println("Source account not found: " + fromAccountNumber);
            return false;
        }

        // Valida se a conta de destino existe
        if (toAccount == null) {
            System.out.println("Destination account not found: " + toAccountNumber);
            return false;
        }

        // Valida se a conta de origem suporta saques
        if (!(fromAccount instanceof Withdrawable)) {
            System.out.println("Source account does not support withdrawals: " + fromAccountNumber);
            return false;
        }

        // Valida se o valor da transferência é positivo
        if (amount <= 0) {
            System.out.println("Transfer amount must be positive");
            return false;
        }

        // Verifica se há saldo suficiente na conta de origem (considerando possível cheque especial)
        double currentBalance = fromAccount.getBalance();
        
        // Se for conta corrente, considera o limite de cheque especial
        if (fromAccount instanceof com.bank.account.CheckingAccount) {
            com.bank.account.CheckingAccount checkingAccount = (com.bank.account.CheckingAccount) fromAccount;
            // Verifica se o saldo após a transferência não excederia o limite negativo permitido
            // Exemplo: se balance = 100, overdraftLimit = 500, pode transferir até 600
            if (currentBalance - amount < -checkingAccount.getOverdraftLimit()) {
                System.out.println("Insufficient funds for transfer from account: " + fromAccountNumber);
                return false;
            }
        } 
        // Se for conta poupança, verifica apenas o saldo (não permite saldo negativo)
        else if (currentBalance < amount) {
            System.out.println("Insufficient funds for transfer from account: " + fromAccountNumber);
            return false;
        }

        try {
            // Realiza o saque da conta de origem usando o padrão Command
            // O WithdrawCommand encapsula a operação e registra no TransactionLogger
            Command withdraw = new WithdrawCommand((Withdrawable) fromAccount, fromAccount, amount);
            withdraw.execute();

            // Realiza o depósito na conta de destino usando o padrão Command
            // O DepositCommand encapsula a operação e registra no TransactionLogger
            Command deposit = new DepositCommand(toAccount, amount);
            deposit.execute();

            // Imprime mensagem de confirmação
            System.out.println("Transfer completed: " + amount + " from " + fromAccountNumber + " to " + toAccountNumber);
            return true;

        } catch (Exception e) {
            // Em caso de erro durante a transferência, exibe mensagem e retorna false
            System.out.println("Error during transfer: " + e.getMessage());
            return false;
        }
    }
}

