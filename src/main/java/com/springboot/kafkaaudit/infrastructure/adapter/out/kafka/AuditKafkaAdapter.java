package com.springboot.kafkaaudit.infrastructure.adapter.out.kafka;

import com.springboot.kafkaaudit.domain.model.AuditEvent;
import com.springboot.kafkaaudit.domain.port.out.AuditPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditKafkaAdapter implements AuditPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String AUDIT_TOPIC = "TOPICO_AUDITORIA";

    @Override
    public void sendAudit(AuditEvent event) {
        // We serialize the event to String for simplicity as per the requirement/design
        // in kafka.md
        // In a real scenario, we might want to serialize to JSON object
        String messagePayload = event.toString();
        kafkaTemplate.send(AUDIT_TOPIC, messagePayload);
    }
}
