package com.bank.factory;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.CheckingAccount;
import com.bank.account.SavingsAccount;

/**
 * Classe {@code AccountFactory} implementa o padrão de projeto **Factory Method**.
 * Ela é responsável por criar instâncias de diferentes tipos de contas bancárias
 * ({@code CheckingAccount}, {@code SavingsAccount}) com base no {@code AccountType} fornecido,
 * sem expor a lógica de instanciação ao código cliente.
 *
 * <p>Este padrão promove o **Princípio Aberto/Fechado (OCP)**, pois novos tipos de conta
 * podem ser adicionados ao sistema modificando apenas esta fábrica, sem a necessidade
 * de alterar o código cliente que solicita a criação de contas. Também contribui para
 * o **Princípio da Inversão de Dependência (DIP)**, pois o cliente depende da abstração
 * {@code Account} e da fábrica, e não das classes concretas de conta.
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Encapsulamento:</b> A factory esconde a lógica de instanciação das classes concretas
 *         (CheckingAccount, SavingsAccount) do cliente. O cliente só precisa fornecer o tipo
 *         e os parâmetros necessários.</li>
 *     <li><b>Decisão de Instanciação:</b> Usa um switch baseado no AccountType para decidir
 *         qual classe concreta instanciar. Para CHECKING, cria CheckingAccount; para SAVINGS,
 *         cria SavingsAccount.</li>
 *     <li><b>Parâmetros Específicos:</b> Cada tipo de conta requer parâmetros diferentes:
 *         <ul>
 *             <li>CHECKING: params[0] = limite de cheque especial</li>
 *             <li>SAVINGS: params[0] = taxa de juros</li>
 *         </ul>
 *     </li>
 *     <li><b>Valores Padrão:</b> Se os parâmetros não forem fornecidos, usa valores padrão
 *         (0.0 para limite de cheque especial ou taxa de juros).</li>
 * </ol>
 */
public class AccountFactory {
    /**
     * Cria uma nova instância de {@code Account} com base no tipo especificado.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Valida se o tipo de conta não é nulo</li>
     *     <li>Usa um switch para decidir qual classe concreta instanciar baseado no tipo</li>
     *     <li>Para CHECKING: cria CheckingAccount com limite de cheque especial (params[0])</li>
     *     <li>Para SAVINGS: cria SavingsAccount com taxa de juros (params[0])</li>
     *     <li>Se params não for fornecido, usa 0.0 como valor padrão</li>
     * </ol>
     * 
     * @param type O tipo de conta a ser criada (CHECKING ou SAVINGS).
     * @param customerName O nome do titular da conta.
     * @param initialBalance O saldo inicial da conta.
     * @param params Parâmetros adicionais específicos do tipo de conta:
     *               - Para CHECKING: params[0] = limite de cheque especial
     *               - Para SAVINGS: params[0] = taxa de juros
     * @return Uma nova instância de {@code Account} do tipo especificado.
     * @throws IllegalArgumentException Se o tipo de conta for desconhecido ou nulo.
     */
    public static Account createAccount(AccountType type, String customerName, double initialBalance, double... params) {
        // Valida se o tipo de conta não é nulo
        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null.");
        }
        
        // Usa switch para decidir qual classe concreta instanciar baseado no tipo
        switch (type) {
            case CHECKING:
                // Para conta corrente, cria CheckingAccount
                // params[0] é o limite de cheque especial (se fornecido, senão usa 0.0)
                return new CheckingAccount(customerName, initialBalance, params.length > 0 ? params[0] : 0.0);
                
            case SAVINGS:
                // Para conta poupança, cria SavingsAccount
                // params[0] é a taxa de juros (se fornecido, senão usa 0.0)
                return new SavingsAccount(customerName, initialBalance, params.length > 0 ? params[0] : 0.0);
                
            default:
                // Se o tipo não for reconhecido, lança exceção
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
}
