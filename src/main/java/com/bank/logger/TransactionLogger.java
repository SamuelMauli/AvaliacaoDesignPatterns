package com.bank.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionLogger {
    private static TransactionLogger instance;
    private PrintWriter writer;
    private static final String LOG_FILE = "transactions.log";

    private TransactionLogger() {
        try {
            FileWriter fw = new FileWriter(LOG_FILE, true); // Append mode
            writer = new PrintWriter(fw, true); // Auto-flush
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized TransactionLogger getInstance() {
        if (instance == null) {
            instance = new TransactionLogger();
        }
        return instance;
    }

    public void log(String message) {
        if (writer != null) {
            writer.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + message);
        }
    }

    // For testing or explicit closing if needed
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
