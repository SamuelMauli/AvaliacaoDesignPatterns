package com.bank.command;

/**
 * Interface {@code Command} define o contrato para todas as operações que podem ser executadas
 * como um comando. Este é o componente central do padrão de projeto **Command**.
 *
 * <p>O padrão Command encapsula uma solicitação como um objeto, permitindo que você
 * parametrize clientes com diferentes solicitações, enfileire ou registre solicitações,
 * e suporte operações desfazíveis. No contexto bancário, cada transação (depósito, saque)
 * pode ser tratada como um comando.
 *
 * <p>Este padrão contribui para o **Princípio Aberto/Fechado (OCP)**, pois novas operações
 * podem ser adicionadas implementando esta interface sem modificar o código existente que
 * invoca os comandos. Também promove o **Princípio da Inversão de Dependência (DIP)**,
 * pois o invocador de comandos depende da abstração {@code Command} e não de implementações
 * concretas de operações.
 */
public interface Command {
    /**
     * Executa a operação encapsulada por este comando.
     */
    void execute();
}
