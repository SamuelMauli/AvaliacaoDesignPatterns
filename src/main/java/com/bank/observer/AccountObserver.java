package com.bank.observer;

import com.bank.account.Account;

/**
 * Interface {@code AccountObserver} define o contrato para observadores de eventos de conta.
 * Este é o componente 'Observer' do padrão de projeto **Observer**.
 *
 * <p>O padrão Observer permite que um objeto (o 'Subject' ou 'Observable', neste caso {@code Account})
 * notifique outros objetos (os 'Observers') sobre quaisquer mudanças em seu estado. Isso promove
 * um acoplamento fraco entre o Subject e seus Observers, pois o Subject não precisa conhecer
 * as classes concretas dos Observers, apenas a interface {@code AccountObserver}.
 *
 * <p>Este padrão contribui para o **Princípio Aberto/Fechado (OCP)**, pois novos observadores
 * podem ser adicionados sem modificar o código da classe {@code Account}. Também adere ao
 * **Princípio da Inversão de Dependência (DIP)**, pois o {@code Account} depende da abstração
 * {@code AccountObserver} e não de implementações concretas de observadores.
 */
public interface AccountObserver {
    /**
     * Método chamado pelo Subject ({@code Account}) para notificar o Observer sobre uma atualização.
     * @param account A conta que sofreu a alteração.
     * @param eventType O tipo de evento que ocorreu (ex: "deposit", "withdraw").
     * @param amount O valor associado ao evento.
     */
    void update(Account account, String eventType, double amount);
}
