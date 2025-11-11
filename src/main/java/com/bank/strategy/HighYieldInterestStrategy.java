package com.bank.strategy;

/**
 * Classe {@code HighYieldInterestStrategy} implementa a interface {@code InterestCalculationStrategy},
 * fornecendo uma estratégia concreta para o cálculo de juros de alto rendimento.
 *
 * <p>Esta classe é uma implementação do padrão **Strategy**, oferecendo uma alternativa
 * ao cálculo de juros simples. A lógica aqui é um exemplo de como uma estratégia de
 * alto rendimento pode ser implementada, adicionando um bônus à taxa de juros base.
 *
 * <p>Contribui para o **Princípio da Responsabilidade Única (SRP)**, pois seu único propósito
 * é calcular juros de alto rendimento, e para o **Princípio Aberto/Fechado (OCP)**, pois
 * permite que novas estratégias de juros sejam adicionadas sem modificar a classe
 * {@code SavingsAccount} ou outras estratégias existentes.
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Fórmula:</b> Juros de alto rendimento são calculados como: juros = saldo × (taxa + bônus).
 *         Adiciona um bônus de 1% (0.01) à taxa de juros base. Por exemplo, se o saldo é R$ 1000
 *         e a taxa é 0.05 (5%), os juros são R$ 1000 × (0.05 + 0.01) = R$ 60.</li>
 *     <li><b>Uso:</b> Esta estratégia pode ser configurada em uma SavingsAccount usando
 *         setInterestStrategy(new HighYieldInterestStrategy()). Permite trocar de juros simples
 *         para alto rendimento dinamicamente.</li>
 *     <li><b>Vantagem:</b> Oferece maior retorno ao cliente comparado à estratégia de juros simples,
 *         incentivando investimentos maiores.</li>
 * </ol>
 */
public class HighYieldInterestStrategy implements InterestCalculationStrategy {
    /**
     * Calcula os juros de alto rendimento com base no saldo e na taxa de juros.
     * Adiciona um bônus de 1% à taxa de juros base.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Adiciona um bônus de 1% (0.01) à taxa de juros base</li>
     *     <li>Multiplica o saldo pela taxa de juros ajustada (taxa + bônus)</li>
     *     <li>Retorna o valor dos juros calculados com o bônus</li>
     * </ol>
     * <p>Fórmula: juros = saldo × (taxa de juros + 0.01)
     * <p>Exemplo: se saldo = 1000 e taxa = 0.05 (5%), juros = 1000 × (0.05 + 0.01) = 1000 × 0.06 = 60
     * 
     * @param balance O saldo atual da conta.
     * @param interestRate A taxa de juros base a ser aplicada (ex: 0.05 = 5% ao ano).
     * @return O valor dos juros calculados com o bônus de 1%.
     */
    @Override
    public double calculateInterest(double balance, double interestRate) {
        // Calcula juros de alto rendimento: adiciona um bônus de 1% (0.01) à taxa de juros base
        // Exemplo: se balance = 1000 e interestRate = 0.05 (5%), retorna 1000 × (0.05 + 0.01) = 60
        // Isso oferece maior retorno ao cliente comparado à estratégia de juros simples
        return balance * (interestRate + 0.01); // 1% de juros bônus
    }
}
