package com.springboot.kafkaaudit.infrastructure.config;

import com.springboot.kafkaaudit.domain.port.out.AuditPort;
import com.springboot.kafkaaudit.domain.service.AuditService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public AuditService auditService(AuditPort auditPort) {
        return new AuditService(auditPort);
    }
}
