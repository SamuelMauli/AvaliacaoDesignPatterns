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
 */
public class AuditService implements AccountObserver {
    private TransactionLogger logger;

    /**
     * Construtor para {@code AuditService}. Obtém a única instância do {@code TransactionLogger}.
     */
    public AuditService() {
        this.logger = TransactionLogger.getInstance();
    }

    /**
     * Método de atualização chamado pelo Subject ({@code Account}) quando um evento ocorre.
     * Registra os detalhes do evento no log de transações.
     * @param account A conta que sofreu a alteração.
     * @param eventType O tipo de evento que ocorreu (ex: "deposit", "withdraw").
     * @param amount O valor associado ao evento.
     */
    @Override
    public void update(Account account, String eventType, double amount) {
        logger.log("AUDIT: Account " + account.getAccountNumber() + ", Event: " + eventType + ", Amount: " + amount + ", Current Balance: " + account.getBalance());
    }
}
