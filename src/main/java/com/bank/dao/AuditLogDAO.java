package com.bank.dao;

import com.bank.model.AuditLog;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface AuditLogDAO {
    void addAuditLog(AuditLog log) throws SQLException;
    List<AuditLog> getAuditLogsByAccountId(UUID accountId) throws SQLException;
}

