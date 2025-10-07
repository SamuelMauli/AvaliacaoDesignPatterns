package com.bank.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe {@code TransactionLogger} implementa o padrão de projeto **Singleton**.
 * Garante que apenas uma instância do logger de transações exista em toda a aplicação,
 * fornecendo um ponto de acesso global para registrar eventos de transação em um arquivo.
 *
 * <p>A escolha do padrão Singleton é justificada pela necessidade de um recurso único e
 * compartilhado para o registro de logs, evitando múltiplas instâncias que poderiam
 * causar conflitos de escrita ou inconsistência nos logs. Além disso, o acesso global
 * simplifica a integração com outras partes do sistema que precisam registrar eventos.
 */
public class TransactionLogger {
    private static TransactionLogger instance;
    private PrintWriter writer;
    private static final String LOG_FILE = "transactions.log";

    /**
     * Construtor privado para evitar a instanciação direta da classe.
     * Inicializa o {@code PrintWriter} para escrever no arquivo de log em modo de anexação.
     */
    private TransactionLogger() {
        try {
            // FileWriter com 'true' para modo de anexação, para não sobrescrever logs existentes
            FileWriter fw = new FileWriter(LOG_FILE, true);
            // PrintWriter com 'true' para auto-flush, garantindo que os logs sejam escritos imediatamente
            writer = new PrintWriter(fw, true);
        } catch (IOException e) {
            // Em caso de erro na criação do arquivo de log, imprime o stack trace.
            // Em um sistema de produção, isso seria tratado de forma mais robusta (ex: logar em console, notificar admin).
            e.printStackTrace();
        }
    }

    /**
     * Retorna a única instância de {@code TransactionLogger}.
     * Utiliza sincronização para garantir a segurança da thread em ambientes multi-threaded.
     * @return A instância única de {@code TransactionLogger}.
     */
    public static synchronized TransactionLogger getInstance() {
        if (instance == null) {
            instance = new TransactionLogger();
        }
        return instance;
    }

    /**
     * Registra uma mensagem de log no arquivo de transações, prefixada com a data e hora atuais.
     * @param message A mensagem a ser registrada.
     */
    public void log(String message) {
        if (writer != null) {
            writer.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + message);
        }
    }

    /**
     * Fecha o {@code PrintWriter}, liberando os recursos do arquivo.
     * Deve ser chamado ao final da aplicação para garantir que todos os logs sejam gravados.
     */
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
