package com.springboot.kafkaaudit.infrastructure.adapter.in.aspect;

import com.springboot.kafkaaudit.application.audit.Auditoria;
import com.springboot.kafkaaudit.domain.port.in.RecordAuditUseCase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @Mock
    private RecordAuditUseCase recordAuditUseCase;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private MethodSignature signature;

    @InjectMocks
    private AuditAspect auditAspect;

    @Test
    void auditMethodExecution_shouldCallRecordAuditUseCase() throws Throwable {
        // Arrange
        Auditoria auditoriaAnnotation = mock(Auditoria.class);
        when(auditoriaAnnotation.value()).thenReturn("Test Event");
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestClass.testMethod()");

        // Act
        auditAspect.auditMethodExecution(pjp, auditoriaAnnotation);

        // Assert
        verify(recordAuditUseCase, times(2)).recordAudit(anyString(), anyString(), eq("SYSTEM")); // Start and End
        verify(pjp).proceed();
    }
}
