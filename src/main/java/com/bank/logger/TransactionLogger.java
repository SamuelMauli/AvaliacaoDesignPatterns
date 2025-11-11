package com.bank.logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe {@code TransactionLogger} implementa o padrão de projeto **Singleton**.
 * Garante que apenas uma instância do logger de transações exista em toda a aplicação,
 * fornecendo um ponto de acesso global para registrar eventos de transação em um arquivo.
 *
 * <p>A escolha do padrão Singleton é justificada pela necessidade de um recurso único e
 * compartilhado para o registro de logs, evitando múltiplas instâncias que poderiam
 * causar conflitos de escrita ou inconsistência nos logs. Além disso, o acesso global
 * simplifica a integração com outras partes do sistema que precisam registrar eventos.
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Singleton:</b> Garante que apenas uma instância do logger exista em toda
 *         a aplicação. Obtida através de getInstance(), que cria a instância na primeira
 *         chamada e retorna a mesma instância em chamadas subsequentes.</li>
 *     <li><b>Arquivo de Log:</b> Todas as transações são registradas no arquivo "transactions.log"
 *         em modo de anexação (append), preservando logs anteriores.</li>
 *     <li><b>Registro:</b> O método log() registra mensagens com timestamp no formato ISO
 *         (YYYY-MM-DDTHH:mm:ss). Cada linha do log contém timestamp e mensagem.</li>
 *     <li><b>Leitura:</b> O método getLogs() lê todas as linhas do arquivo de log e retorna
 *         como lista de strings, permitindo exibir o histórico na GUI.</li>
 * </ol>
 */
public class TransactionLogger {
    /** Instância única do TransactionLogger (Singleton) */
    private static TransactionLogger instance;
    
    /** PrintWriter usado para escrever no arquivo de log */
    private PrintWriter writer;
    
    /** Nome do arquivo de log onde todas as transações são registradas */
    private static final String LOG_FILE = "transactions.log";

    /**
     * Construtor privado para evitar a instanciação direta da classe.
     * Inicializa o {@code PrintWriter} para escrever no arquivo de log em modo de anexação.
     * <p><b>LÓGICA:</b> O construtor é privado para implementar o padrão Singleton.
     * Inicializa o PrintWriter em modo de anexação (append) para preservar logs anteriores,
     * e com auto-flush para garantir que os logs sejam escritos imediatamente.
     */
    private TransactionLogger() {
        try {
            // FileWriter com 'true' para modo de anexação (append)
            // Isso garante que logs anteriores não sejam sobrescritos
            FileWriter fw = new FileWriter(LOG_FILE, true);
            // PrintWriter com 'true' para auto-flush
            // Isso garante que os logs sejam escritos imediatamente no arquivo
            writer = new PrintWriter(fw, true);
        } catch (IOException e) {
            // Em caso de erro na criação do arquivo de log, imprime o stack trace
            // Em um sistema de produção, isso seria tratado de forma mais robusta
            // (ex: logar em console, notificar admin, usar fallback)
            e.printStackTrace();
        }
    }

    /**
     * Retorna a única instância de {@code TransactionLogger}.
     * Utiliza sincronização para garantir a segurança da thread em ambientes multi-threaded.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Verifica se a instância já foi criada</li>
     *     <li>Se não foi criada, cria uma nova instância (chama o construtor privado)</li>
     *     <li>Retorna a instância (criada agora ou já existente)</li>
     * </ol>
     * <p>A sincronização (synchronized) garante que apenas uma thread possa criar a instância
     * em ambientes multi-threaded, evitando race conditions.
     * 
     * @return A instância única de {@code TransactionLogger}.
     */
    public static synchronized TransactionLogger getInstance() {
        // Verifica se a instância já foi criada
        if (instance == null) {
            // Se não foi criada, cria uma nova instância (chama o construtor privado)
            instance = new TransactionLogger();
        }
        // Retorna a instância (criada agora ou já existente)
        return instance;
    }

    /**
     * Registra uma mensagem de log no arquivo de transações, prefixada com a data e hora atuais.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Obtém a data e hora atual usando LocalDateTime.now()</li>
     *     <li>Formata o timestamp no formato ISO (YYYY-MM-DDTHH:mm:ss)</li>
     *     <li>Escreve no arquivo de log: timestamp + " - " + mensagem</li>
     * </ol>
     * <p>O formato do log é: "YYYY-MM-DDTHH:mm:ss - mensagem"
     * 
     * @param message A mensagem a ser registrada (ex: "Deposit: Account XXX, Amount: 100.0").
     */
    public void log(String message) {
        // Verifica se o PrintWriter foi inicializado corretamente
        if (writer != null) {
            // Obtém a data e hora atual e formata no formato ISO (YYYY-MM-DDTHH:mm:ss)
            // Exemplo: "2024-01-15T14:30:45 - Deposit: Account XXX, Amount: 100.0"
            writer.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + message);
        }
    }

    /**
     * Retorna todas as entradas de log do arquivo de transações.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Abre o arquivo de log para leitura usando BufferedReader</li>
     *     <li>Lê todas as linhas do arquivo uma por uma</li>
     *     <li>Adiciona cada linha à lista de logs</li>
     *     <li>Retorna a lista completa de logs</li>
     * </ol>
     * <p>Este método é usado pela GUI para exibir o histórico de transações na tabela.
     * 
     * @return Uma lista de strings, onde cada string é uma linha do arquivo de log.
     */
    public List<String> getLogs() {
        // Cria uma lista vazia para armazenar os logs
        List<String> logs = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            // Lê o arquivo linha por linha
            String line;
            while ((line = reader.readLine()) != null) {
                // Adiciona cada linha à lista de logs
                logs.add(line);
            }
        } catch (IOException e) {
            // Em caso de erro ao ler o arquivo de log, exibe mensagem de erro
            System.err.println("Erro ao ler o arquivo de log: " + e.getMessage());
        }
        
        // Retorna a lista completa de logs
        return logs;
    }

    /**
     * Fecha o {@code PrintWriter}, liberando os recursos do arquivo.
     * Deve ser chamado ao final da aplicação para garantir que todos os logs sejam gravados.
     * <p><b>LÓGICA:</b> Fecha o PrintWriter, garantindo que todos os dados em buffer sejam
     * escritos no arquivo antes de fechar. Isso é importante para garantir que nenhum log
     * seja perdido quando a aplicação terminar.
     */
    public void close() {
        // Verifica se o PrintWriter foi inicializado
        if (writer != null) {
            // Fecha o PrintWriter, garantindo que todos os dados em buffer sejam escritos
            writer.close();
        }
    }
}

