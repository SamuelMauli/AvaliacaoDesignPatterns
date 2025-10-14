package com.bank.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLog {
    private UUID id;
    private UUID accountId;
    private String eventType;
    private String eventData;
    private LocalDateTime createdAt;

    public AuditLog(UUID id, UUID accountId, String eventType, String eventData, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventData() {
        return eventData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
               "id=" + id +
               ", accountId=" + accountId +
               ", eventType='" + eventType + '\'' +
               ", eventData='" + eventData + '\'' +
               ", createdAt=" + createdAt +
               '}'
        ;
    }
}

