package com.bank.dao;

import com.bank.config.DatabaseConfig;
import com.bank.model.AuditLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuditLogDAOImpl implements AuditLogDAO {

    @Override
    public void addAuditLog(AuditLog log) throws SQLException {
        String sql = "INSERT INTO audit_logs (id, account_id, event_type, event_data, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, log.getId());
            stmt.setObject(2, log.getAccountId());
            stmt.setString(3, log.getEventType());
            stmt.setString(4, log.getEventData());
            stmt.setTimestamp(5, Timestamp.valueOf(log.getCreatedAt()));
            stmt.executeUpdate();
        }
    }

    @Override
    public List<AuditLog> getAuditLogsByAccountId(UUID accountId) throws SQLException {
        List<AuditLog> auditLogs = new ArrayList<>();
        String sql = "SELECT id, account_id, event_type, event_data, created_at FROM audit_logs WHERE account_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog(
                            (UUID) rs.getObject("id"),
                            (UUID) rs.getObject("account_id"),
                            rs.getString("event_type"),
                            rs.getString("event_data"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    auditLogs.add(log);
                }
            }
        }
        return auditLogs;
    }
}

