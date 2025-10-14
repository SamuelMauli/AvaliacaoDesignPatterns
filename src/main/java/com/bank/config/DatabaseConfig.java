package com.bank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String DB_URL = System.getenv("SUPABASE_DB_URL");
    private static final String DB_USER = System.getenv("SUPABASE_DB_USER");
    private static final String DB_PASSWORD = System.getenv("SUPABASE_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            throw new SQLException("Supabase database credentials are not set in environment variables.");
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}

