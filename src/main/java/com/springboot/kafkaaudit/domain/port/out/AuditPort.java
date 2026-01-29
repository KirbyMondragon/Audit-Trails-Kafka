package com.springboot.kafkaaudit.domain.port.out;

import com.springboot.kafkaaudit.domain.model.AuditEvent;

public interface AuditPort {
    void sendAudit(AuditEvent event);
}
