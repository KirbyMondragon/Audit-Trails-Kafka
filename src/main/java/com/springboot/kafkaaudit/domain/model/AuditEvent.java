package com.springboot.kafkaaudit.domain.model;

import java.time.LocalDateTime;

@lombok.Getter
@lombok.ToString
public class AuditEvent {
    private final String eventType;
    private final String message;
    private final String user;
    private final LocalDateTime timestamp;

    public AuditEvent(String eventType, String message, String user) {
        this.eventType = eventType;
        this.message = message;
        this.user = user;
        this.timestamp = LocalDateTime.now();
    }
}
