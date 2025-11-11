package com.bank.strategy;

/**
 * Classe {@code SimpleInterestStrategy} implementa a interface {@code InterestCalculationStrategy},
 * fornecendo uma estratégia concreta para o cálculo de juros simples.
 *
 * <p>Esta classe é uma implementação do padrão **Strategy**, onde o algoritmo de cálculo de juros
 * é encapsulado e pode ser trocado dinamicamente. A lógica aqui é simples: juros são calculados
 * como uma porcentagem direta do saldo atual.
 *
 * <p>Contribui para o **Princípio da Responsabilidade Única (SRP)**, pois seu único propósito
 * é calcular juros simples, e para o **Princípio Aberto/Fechado (OCP)**, pois novas estratégias
 * de juros podem ser adicionadas sem modificar esta classe ou a {@code SavingsAccount}.
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Fórmula:</b> Juros simples são calculados como: juros = saldo × taxa de juros.
 *         Por exemplo, se o saldo é R$ 1000 e a taxa é 0.05 (5%), os juros são R$ 50.</li>
 *     <li><b>Uso:</b> Esta estratégia é usada por padrão em SavingsAccount quando a conta
 *         é criada. Pode ser trocada dinamicamente usando setInterestStrategy().</li>
 *     <li><b>Flexibilidade:</b> O padrão Strategy permite que diferentes algoritmos de cálculo
 *         de juros sejam usados (juros simples, compostos, alto rendimento, etc.) sem modificar
 *         a classe SavingsAccount.</li>
 * </ol>
 */
public class SimpleInterestStrategy implements InterestCalculationStrategy {
    /**
     * Calcula os juros simples com base no saldo e na taxa de juros.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Multiplica o saldo atual pela taxa de juros</li>
     *     <li>Retorna o valor dos juros calculados</li>
     * </ol>
     * <p>Fórmula: juros = saldo × taxa de juros
     * <p>Exemplo: se saldo = 1000 e taxa = 0.05 (5%), juros = 1000 × 0.05 = 50
     * 
     * @param balance O saldo atual da conta.
     * @param interestRate A taxa de juros a ser aplicada (ex: 0.05 = 5% ao ano).
     * @return O valor dos juros calculados.
     */
    @Override
    public double calculateInterest(double balance, double interestRate) {
        // Calcula juros simples: multiplica o saldo pela taxa de juros
        // Exemplo: se balance = 1000 e interestRate = 0.05 (5%), retorna 50
        return balance * interestRate;
    }
}
