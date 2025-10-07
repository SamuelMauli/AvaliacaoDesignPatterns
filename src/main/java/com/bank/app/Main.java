package com.bank.app;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.SavingsAccount;
import com.bank.decorator.OverdraftProtectionDecorator;
import com.bank.facade.BankingFacade;
import com.bank.observer.AuditService;
import com.bank.strategy.HighYieldInterestStrategy;
import com.bank.logger.TransactionLogger;

/**
 * Classe principal {@code Main} para demonstrar a funcionalidade do sistema bancário
 * e a aplicação dos padrões de projeto e princípios SOLID.
 * Esta classe atua como o cliente que interage com a {@code BankingFacade},
 * que por sua vez orquestra as operações com os subsistemas.
 *
 * <p>A demonstração inclui:
 * <ul>
 *     <li>Criação de contas (Factory Method)</li>
 *     <li>Depósitos e Saques (Command Pattern)</li>
 *     <li>Proteção contra Cheque Especial (Decorator Pattern)</li>
 *     <li>Cálculo de Juros com Estratégias Variáveis (Strategy Pattern)</li>
 *     <li>Auditoria de Transações (Observer Pattern e Singleton para Logger)</li>
 * </ul>
 */
public class Main {
    /**
     * Método principal que executa a demonstração do sistema bancário.
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // 1. Inicializa a Fachada Bancária (BankingFacade)
        // Padrão: Facade - Simplifica a interação com o subsistema bancário complexo.
        // O cliente (Main) não precisa conhecer os detalhes de criação de contas, comandos, etc.
        BankingFacade bankingFacade = new BankingFacade();

        // 2. Inicializa o Serviço de Auditoria (AuditService)
        // Padrão: Observer - O AuditService é um observador que reage a eventos da conta.
        // Ele será anexado às contas para registrar suas atividades.
        AuditService auditService = new AuditService();

        // 3. Criação de Contas (Factory Method)
        System.out.println("\n--- Criando Contas ---");

        // Cria uma Conta Corrente para Alice
        // Padrão: Factory Method - A BankingFacade usa AccountFactory internamente para criar a conta
        // sem expor a lógica de instanciação de CheckingAccount.
        String checkingAccNum = bankingFacade.createAccount(AccountType.CHECKING, "Alice Smith", 1000.0, 500.0); // Saldo inicial, limite de cheque especial
        Account aliceChecking = bankingFacade.getAccount(checkingAccNum);
        if (aliceChecking != null) {
            // Anexa o serviço de auditoria à conta de Alice
            // Padrão: Observer - AliceChecking é o Subject, AuditService é o Observer.
            aliceChecking.addObserver(auditService);
        }

        // Cria uma Conta Poupança para Bob
        // Padrão: Factory Method - Similarmente, cria SavingsAccount.
        String savingsAccNum = bankingFacade.createAccount(AccountType.SAVINGS, "Bob Johnson", 2000.0, 0.05); // Saldo inicial, taxa de juros
        Account bobSavings = bankingFacade.getAccount(savingsAccNum);
        if (bobSavings != null) {
            // Anexa o serviço de auditoria à conta de Bob
            // Padrão: Observer
            bobSavings.addObserver(auditService);
        }

        // 4. Demonstração de Depósito (Command Pattern)
        System.out.println("\n--- Demonstrando Depósito ---");
        // Padrão: Command - A BankingFacade cria e executa um DepositCommand.
        // A operação de depósito é encapsulada em um objeto, permitindo flexibilidade.
        bankingFacade.deposit(checkingAccNum, 200.0);
        bankingFacade.deposit(savingsAccNum, 500.0);

        // 5. Demonstração de Saque (Command Pattern)
        System.out.println("\n--- Demonstrando Saque ---");
        // Padrão: Command - A BankingFacade cria e executa um WithdrawCommand.
        // A operação de saque é encapsulada em um objeto.
        bankingFacade.withdraw(checkingAccNum, 300.0);
        bankingFacade.withdraw(savingsAccNum, 1000.0);

        // 6. Demonstração de Proteção contra Cheque Especial (Decorator Pattern)
        System.out.println("\n--- Demonstrando Proteção contra Cheque Especial (Decorator) ---");
        // A conta corrente de Alice tem um saldo de 900.0 (1000 + 200 - 300) e limite de cheque especial de 500.0.
        // Tentativa de saque que excede o saldo, mas está dentro do limite de cheque especial.
        bankingFacade.withdraw(checkingAccNum, 1000.0); // Saldo: 900.0. Saque de 1000.0 usa 100.0 do cheque especial.

        // Cria uma nova Conta Corrente e aplica o OverdraftProtectionDecorator explicitamente
        // Isso demonstra como o Decorator pode ser aplicado a qualquer Account que implemente Withdrawable.
        System.out.println("\n--- Criando uma nova conta com Decorator de Proteção contra Cheque Especial ---");
        String decoratedAccNum = bankingFacade.createAccount(AccountType.CHECKING, "Charlie Brown", 500.0, 0.0); // Conta sem cheque especial inicial
        Account charlieChecking = bankingFacade.getAccount(decoratedAccNum);
        if (charlieChecking != null) {
            charlieChecking.addObserver(auditService);
            // Padrão: Decorator - Envolve a conta de Charlie com um decorador de cheque especial.
            // Agora, `decoratedCharlie` tem a funcionalidade de cheque especial.
            OverdraftProtectionDecorator decoratedCharlie = new OverdraftProtectionDecorator(charlieChecking, 200.0);

            System.out.println("Tentando saque na conta decorada de Charlie...");
            decoratedCharlie.withdraw(600.0); // Saldo: 500.0. Saque de 600.0 usa 100.0 do cheque especial de 200.0.
            System.out.println("Saldo da conta decorada de Charlie: " + decoratedCharlie.getBalance());
            decoratedCharlie.withdraw(200.0); // Saldo: -100.0. Saque de 200.0 excede o limite de -200.0.
            System.out.println("Saldo da conta decorada de Charlie: " + decoratedCharlie.getBalance());
            decoratedCharlie.withdraw(100.0); // Saldo: -300.0. Saque de 100.0 excede o limite de -200.0.
        }

        // 7. Demonstração de Cálculo de Juros (Strategy Pattern)
        System.out.println("\n--- Demonstrando Cálculo de Juros (Strategy) ---");
        if (bobSavings instanceof SavingsAccount) {
            SavingsAccount bobSavingsAccount = (SavingsAccount) bobSavings;
            System.out.println("Saldo da conta poupança de Bob antes dos juros: " + bobSavingsAccount.getBalance());
            // Padrão: Strategy - Usa a estratégia de juros padrão (SimpleInterestStrategy).
            bobSavingsAccount.calculateInterest();
            System.out.println("Saldo da conta poupança de Bob após juros simples: " + bobSavingsAccount.getBalance());

            // Altera a estratégia de cálculo de juros para alto rendimento
            System.out.println("\n--- Alterando Estratégia de Cálculo de Juros para Alto Rendimento ---");
            // Padrão: Strategy - Altera a estratégia em tempo de execução.
            bobSavingsAccount.setInterestStrategy(new HighYieldInterestStrategy());
            System.out.println("Saldo da conta poupança de Bob antes dos juros de alto rendimento: " + bobSavingsAccount.getBalance());
            bobSavingsAccount.calculateInterest();
            System.out.println("Saldo da conta poupança de Bob após juros de alto rendimento: " + bobSavingsAccount.getBalance());
        }

        // 8. Verificação do Log de Auditoria (Observer Pattern e Singleton Logger)
        System.out.println("\n--- Verificando Log de Auditoria (Verifique o arquivo transactions.log) ---");
        // Padrão: Singleton - O TransactionLogger garante que todas as mensagens de log
        // sejam escritas no mesmo arquivo 'transactions.log'.
        // Padrão: Observer - O AuditService registrou todas as operações acima no log.
        // Em uma aplicação real, você leria e exibiria o conteúdo deste arquivo.

        // Fecha o logger para garantir que todas as mensagens sejam descarregadas no arquivo.
        TransactionLogger.getInstance().close();
    }
}

