package com.springboot.kafkaaudit.infrastructure.adapter.in.aspect;

import com.springboot.kafkaaudit.application.audit.Auditoria;
import com.springboot.kafkaaudit.domain.port.in.RecordAuditUseCase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final RecordAuditUseCase recordAuditUseCase;

    @Around("@annotation(auditoriaAnnotation)")
    public Object auditMethodExecution(ProceedingJoinPoint pjp, Auditoria auditoriaAnnotation) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        String eventDescription = auditoriaAnnotation.value();

        // INICIO
        recordAuditUseCase.recordAudit("INICIO", String.format("%s - %s", eventDescription, methodName), "SYSTEM");

        try {
            Object result = pjp.proceed();
            // FIN
            recordAuditUseCase.recordAudit("FIN", String.format("%s - %s", eventDescription, methodName), "SYSTEM");
            return result;
        } catch (Exception ex) {
            // ERROR
            recordAuditUseCase.recordAudit("ERROR",
                    String.format("%s - %s. Causa: %s", eventDescription, methodName, ex.getMessage()), "SYSTEM");
            throw ex;
        }
    }
}
