package com.springboot.kafkaaudit.domain.service;

import com.springboot.kafkaaudit.domain.model.AuditEvent;
import com.springboot.kafkaaudit.domain.port.in.RecordAuditUseCase;
import com.springboot.kafkaaudit.domain.port.out.AuditPort;

@lombok.RequiredArgsConstructor
public class AuditService implements RecordAuditUseCase {

    private final AuditPort auditPort;

    @Override
    public void recordAudit(String eventType, String message, String user) {
        // Here we can apply business logic, like filtering specific users or formatting
        // messages
        AuditEvent event = new AuditEvent(eventType, message, user);
        auditPort.sendAudit(event);
    }
}
