package com.bank.account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.bank.observer.AccountObserver;

/**
 * Classe abstrata {@code Account} serve como a base para todos os tipos de contas bancárias.
 * Ela encapsula atributos comuns como número da conta, saldo e nome do cliente, e fornece
 * a funcionalidade básica de depósito. Além disso, implementa o padrão **Observer** para
 * notificar partes interessadas sobre mudanças no estado da conta.
 *
 * <p>Esta classe adere a vários princípios SOLID:
 * <ul>
 *     <li>**Princípio da Responsabilidade Única (SRP)**: A classe {@code Account} é primariamente
 *         responsável por gerenciar o estado básico de uma conta (saldo, número, cliente) e a lógica
 *         de depósito. Outras responsabilidades, como saque ou cálculo de juros, são delegadas
 *         a interfaces específicas ou subclasses.</li>
 *     <li>**Princípio Aberto/Fechado (OCP)**: A classe é aberta para extensão (novos tipos de conta
 *         podem estender {@code Account}) mas fechada para modificação (o código cliente que opera
 *         em {@code Account} não precisa ser alterado para suportar novos tipos de conta).</li>
 *     <li>**Princípio da Substituição de Liskov (LSP)**: Subtipos de {@code Account} (como
 *         {@code CheckingAccount} e {@code SavingsAccount}) devem ser substituíveis por {@code Account}
 *         sem alterar a correção do programa. Métodos como {@code deposit} são implementados de forma
 *         consistente.</li>
 *     <li>**Princípio da Inversão de Dependência (DIP)**: A classe depende de abstrações ({@code AccountObserver})
 *         em vez de implementações concretas, permitindo um acoplamento fraco.</li>
 * </ul>
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Criação da Conta:</b> Quando uma conta é criada, um número único é gerado usando UUID,
 *         o nome do cliente e o saldo inicial são armazenados, e uma lista vazia de observadores é inicializada.</li>
 *     <li><b>Depósito:</b> O método deposit() verifica se o valor é positivo, adiciona ao saldo usando
 *         adjustBalanceAndNotify(), que também notifica todos os observadores registrados sobre o evento.</li>
 *     <li><b>Observer Pattern:</b> Qualquer classe que implemente AccountObserver pode ser registrada
 *         para receber notificações quando eventos ocorrem na conta (depósito, saque, etc.).</li>
 *     <li><b>Notificação de Observadores:</b> Sempre que o saldo é modificado através de adjustBalanceAndNotify(),
 *         todos os observadores são notificados com o tipo de evento e o valor envolvido.</li>
 * </ol>
 */
public abstract class Account implements Depositable {
    /** Número único da conta, gerado automaticamente usando UUID */
    protected String accountNumber;
    
    /** Saldo atual da conta em reais */
    protected double balance;
    
    /** Nome completo do titular da conta */
    protected String customerName;
    
    /** Lista de observadores que serão notificados sobre mudanças na conta.
     *  Implementa o padrão Observer - permite que outras classes sejam notificadas
     *  quando eventos ocorrem na conta (ex: depósito, saque) */
    private List<AccountObserver> observers = new ArrayList<>();

    /**
     * Construtor para criar uma nova conta.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Gera um número de conta único usando UUID.randomUUID().toString() - isso garante
     *         que cada conta tenha um identificador único no sistema</li>
     *     <li>Armazena o nome do cliente fornecido</li>
     *     <li>Define o saldo inicial da conta</li>
     *     <li>A lista de observadores é inicializada vazia (será populada posteriormente se necessário)</li>
     * </ol>
     * 
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     */
    public Account(String customerName, double initialBalance) {
        // Gera um número de conta único usando UUID - garante unicidade no sistema
        this.accountNumber = UUID.randomUUID().toString();
        // Armazena o nome do cliente
        this.customerName = customerName;
        // Define o saldo inicial da conta
        this.balance = initialBalance;
        // A lista de observadores já foi inicializada como ArrayList vazio no campo
    }

    /**
     * Retorna o número da conta.
     * @return O número da conta.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Retorna o saldo atual da conta.
     * @return O saldo da conta.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Retorna o nome do titular da conta.
     * @return O nome do titular da conta.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Adiciona um observador à lista de observadores da conta.
     * <p><b>LÓGICA:</b> Adiciona o observador fornecido à lista interna de observadores.
     * A partir deste momento, o observador receberá notificações sobre todos os eventos
     * que ocorrerem nesta conta (depósitos, saques, etc.).
     * 
     * @param observer O observador a ser adicionado (ex: AuditService para auditoria).
     */
    public void addObserver(AccountObserver observer) {
        // Adiciona o observador à lista - ele será notificado de todos os eventos futuros
        observers.add(observer);
    }

    /**
     * Remove um observador da lista de observadores da conta.
     * <p><b>LÓGICA:</b> Remove o observador da lista, fazendo com que ele não receba mais
     * notificações sobre eventos desta conta.
     * 
     * @param observer O observador a ser removido.
     */
    public void removeObserver(AccountObserver observer) {
        // Remove o observador da lista - ele não receberá mais notificações
        observers.remove(observer);
    }

    /**
     * Notifica todos os observadores registrados sobre um evento na conta.
     * <p><b>LÓGICA:</b> Itera sobre todos os observadores na lista e chama o método update()
     * de cada um, passando a conta, o tipo de evento e o valor envolvido. Isso permite que
     * múltiplos serviços (ex: auditoria, logging, notificações) sejam informados sobre o evento.
     * 
     * @param eventType O tipo de evento (ex: "deposit", "withdraw", "interest_calculation").
     * @param amount O valor associado ao evento.
     */
    public void notifyObservers(String eventType, double amount) {
        // Itera sobre todos os observadores registrados
        for (AccountObserver observer : observers) {
            // Chama o método update() de cada observador, passando:
            // - this: a conta que sofreu o evento
            // - eventType: o tipo de evento (deposit, withdraw, etc.)
            // - amount: o valor envolvido no evento
            observer.update(this, eventType, amount);
        }
    }

    /**
     * Método protegido para ajustar o saldo da conta e notificar os observadores.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Adiciona o valor fornecido ao saldo atual (pode ser positivo para depósito
     *         ou negativo para saque)</li>
     *     <li>Notifica todos os observadores registrados sobre o evento, passando o tipo
     *         de evento e o valor envolvido</li>
     * </ol>
     * Este método é utilizado internamente por subclasses e decoradores para modificar o saldo
     * de forma controlada e garantir que as notificações sejam sempre enviadas. Centraliza a
     * lógica de atualização de saldo e notificação, evitando duplicação de código.
     * 
     * @param amount O valor pelo qual o saldo será ajustado (positivo para aumento, negativo para diminuição).
     * @param eventType O tipo de evento a ser notificado (ex: "deposit", "withdraw", "interest_calculation").
     */
    public void adjustBalanceAndNotify(double amount, String eventType) {
        // Atualiza o saldo da conta: adiciona o valor (pode ser positivo ou negativo)
        this.balance += amount;
        // Notifica todos os observadores registrados sobre o evento
        // Isso permite que serviços como auditoria, logging, etc. sejam informados
        notifyObservers(eventType, amount);
    }

    /**
     * Implementação da operação de depósito. O valor é adicionado ao saldo e os observadores são notificados.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Valida se o valor do depósito é positivo (não permite depósitos negativos ou zero)</li>
     *     <li>Se válido, chama adjustBalanceAndNotify() que:
     *         <ul>
     *             <li>Adiciona o valor ao saldo</li>
     *             <li>Notifica todos os observadores sobre o depósito</li>
     *         </ul>
     *     </li>
     *     <li>Imprime uma mensagem de confirmação no console</li>
     * </ol>
     * 
     * @param amount O valor a ser depositado (deve ser positivo).
     */
    @Override
    public void deposit(double amount) {
        // Valida se o valor do depósito é positivo
        if (amount > 0) {
            // Chama adjustBalanceAndNotify que:
            // 1. Adiciona o valor ao saldo (balance += amount)
            // 2. Notifica todos os observadores sobre o evento "deposit"
            adjustBalanceAndNotify(amount, "deposit");
            // Imprime mensagem de confirmação no console
            System.out.println("Deposit of " + amount + " to account " + accountNumber + ". New balance: " + balance);
        } else {
            // Se o valor não for positivo, exibe mensagem de erro
            System.out.println("Deposit amount must be positive.");
        }
    }

    /**
     * Método abstrato para obter o tipo específico da conta.
     * @return Uma string representando o tipo da conta (ex: "Checking Account", "Savings Account").
     */
    public abstract String getAccountType();
}
