package com.springboot.kafkaaudit.domain.port.in;

public interface RecordAuditUseCase {
    void recordAudit(String eventType, String message, String user);
}
