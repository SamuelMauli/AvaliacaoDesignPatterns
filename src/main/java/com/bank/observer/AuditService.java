package com.bank.observer;

import com.bank.account.Account;
import com.bank.logger.TransactionLogger;

/**
 * Classe {@code AuditService} implementa a interface {@code AccountObserver},
 * atuando como um observador concreto no padrão de projeto **Observer**.
 * Sua responsabilidade é registrar todas as operações significativas que ocorrem
 * nas contas bancárias para fins de auditoria.
 *
 * <p>Como um Observer, o {@code AuditService} é notificado sempre que um evento
 * ocorre em uma {@code Account} que ele está observando. Ele então utiliza o
 * {@code TransactionLogger} (que é um **Singleton**) para persistir esses eventos
 * em um arquivo de log.
 *
 * <p>Este design promove o **Princípio da Responsabilidade Única (SRP)**, pois o
 * {@code AuditService} é focado exclusivamente na tarefa de auditoria. Também adere
 * ao **Princípio Aberto/Fechado (OCP)**, permitindo que novas formas de auditoria
 * ou outros observadores sejam adicionados sem modificar a lógica central da {@code Account}.
 * A dependência da abstração {@code AccountObserver} e do {@code TransactionLogger}
 * (acessado via seu método estático {@code getInstance()}) demonstra o
 * **Princípio da Inversão de Dependência (DIP)**.
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Registro:</b> O AuditService é registrado como observador em uma conta
 *         usando account.addObserver(this). A partir deste momento, recebe notificações
 *         sobre todos os eventos que ocorrem na conta.</li>
 *     <li><b>Notificação:</b> Quando um evento ocorre na conta (depósito, saque, etc.),
 *         a conta chama notifyObservers(), que por sua vez chama update() de todos os
 *         observadores registrados, incluindo o AuditService.</li>
 *     <li><b>Registro de Auditoria:</b> O método update() recebe os detalhes do evento
 *         (conta, tipo de evento, valor) e registra no TransactionLogger (Singleton)
 *         para fins de auditoria e histórico.</li>
 * </ol>
 */
public class AuditService implements AccountObserver {
    /** Logger de transações (Singleton) usado para registrar eventos de auditoria */
    private TransactionLogger logger;

    /**
     * Construtor para {@code AuditService}. Obtém a única instância do {@code TransactionLogger}.
     * <p><b>LÓGICA:</b> Obtém a instância única do TransactionLogger (Singleton) que será
     * usado para registrar todos os eventos de auditoria no arquivo de log.
     */
    public AuditService() {
        // Obtém a instância única do TransactionLogger (Singleton)
        // Isso garante que todas as mensagens de auditoria sejam escritas no mesmo arquivo de log
        this.logger = TransactionLogger.getInstance();
    }

    /**
     * Método de atualização chamado pelo Subject ({@code Account}) quando um evento ocorre.
     * Registra os detalhes do evento no log de transações.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Este método é chamado automaticamente pela conta quando um evento ocorre
     *         (depósito, saque, cálculo de juros, etc.) através do padrão Observer</li>
     *     <li>Recebe os detalhes do evento: conta que sofreu a alteração, tipo de evento
     *         e valor envolvido</li>
     *     <li>Registra uma mensagem de auditoria no TransactionLogger (Singleton) com:
     *         número da conta, tipo de evento, valor envolvido e saldo atual</li>
     * </ol>
     * <p>Este método é chamado automaticamente pelo sistema sempre que um evento ocorre
     * em uma conta que o AuditService está observando, sem necessidade de chamada manual.
     * 
     * @param account A conta que sofreu a alteração.
     * @param eventType O tipo de evento que ocorreu (ex: "deposit", "withdraw", "interest_calculation").
     * @param amount O valor associado ao evento.
     */
    @Override
    public void update(Account account, String eventType, double amount) {
        // Registra uma mensagem de auditoria no TransactionLogger (Singleton)
        // O log inclui: número da conta, tipo de evento, valor envolvido e saldo atual
        // Isso permite rastrear todas as operações realizadas no sistema para fins de auditoria
        logger.log("AUDIT: Account " + account.getAccountNumber() + ", Event: " + eventType + ", Amount: " + amount + ", Current Balance: " + account.getBalance());
    }
}
