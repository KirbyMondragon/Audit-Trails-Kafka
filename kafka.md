Guía Definitiva: Pistas de Auditoría con Kafka en Arquitectura Hexagonal
1. Resumen y Principios Clave

El objetivo es implementar auditorías enviadas a Kafka manteniendo la pureza del dominio y el desacoplamiento.

    Dominio Puro: No debe tener dependencias de frameworks (Spring, Kafka).

    Puertos (Interfaces): Definen el "qué" necesita el negocio (ej. AuditPort).

    Adaptadores (Infraestructura): Implementaciones técnicas (ej. AuditKafkaAdapter).

    Uso Controlado de Frameworks: Las anotaciones y aspectos viven fuera del dominio.

2. Estrategias de Implementación
A. Auditoría por Anotaciones (Declarativa)

Ideal para auditorías de alto nivel (inicio, fin, error) en puntos de entrada.

    Ventaja: No invasiva, reutilizable y centralizada.

Paso 1: Definir la Anotación (Capa Application)
Java

// Ubicación: src/main/java/com/example/application/audit/Auditoria.java
package com.example.application.audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditoria {
    String value(); // Descripción del evento de negocio
}

Paso 2: Crear el Aspecto (Capa Infrastructure)
Java

// Ubicación: src/main/java/com/example/infrastructure/audit/AuditAspect.java
@Aspect
@Component
public class AuditAspect {
    private final AuditPort auditPort;

    public AuditAspect(AuditPort auditPort) {
        this.auditPort = auditPort;
    }

    @Around("@annotation(auditoriaAnnotation)")
    public Object auditMethodExecution(ProceedingJoinPoint pjp, Auditoria auditoriaAnnotation) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        String eventDescription = auditoriaAnnotation.value();

        auditPort.sendAudit(String.format("INICIO: %s - %s", eventDescription, methodName));
        try {
            Object result = pjp.proceed();
            auditPort.sendAudit(String.format("FIN: %s - %s", eventDescription, methodName));
            return result;
        } catch (Exception ex) {
            auditPort.sendAudit(String.format("ERROR: %s - %s. Causa: %s", eventDescription, methodName, ex.getMessage()));
            throw ex;
        }
    }
}

B. Auditoría Programática (Explícita)

Se usa cuando la lógica de negocio exige registrar un evento específico en un punto exacto de un algoritmo.

    Ventaja: Precisión quirúrgica y acceso al estado actual del modelo de dominio.

Paso 1: Definir el Puerto de Salida (Capa Domain)
Java

package com.example.domain.port.out;

public interface AuditPort {
    void sendAudit(String message);
}

Paso 2: Uso en el Servicio de Dominio
Java

public class PolicyService {
    private final AuditPort auditPort;

    public void authorizePolicy(Policy policy) {
        // ... lógica de negocio ...
        if (policy.requiresSpecialValidation()) {
            auditPort.sendAudit(String.format("AUDITORIA DE NEGOCIO: Póliza %s requiere validación especial.", policy.getId()));
        }
    }
}

3. Implementación de Infraestructura (Kafka)
Adaptador de Salida
Java

@Component
@RequiredArgsConstructor
public class AuditKafkaAdapter implements AuditPort {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String AUDIT_TOPIC = "TOPICO_AUDITORIA";

    @Override
    public void sendAudit(String evento) {
        kafkaTemplate.send(AUDIT_TOPIC, evento);
    }
}

Configuración del Productor
Java

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

4. Resumen de Aplicación por Capa
Capa	¿Anotación @Auditoria?	¿Programática (AuditPort)?
Infra/Input (Controller)	✅ SÍ	❌ NO
Application (UseCase)	✅ SÍ	✅ SÍ
Domain (Service)	❌ NO	✅ SÍ (Solo si el negocio lo exige)
Infra/Output (Repo)	❌ NO	✅ SÍ (Solo por norma técnica)
Reglas de Oro 💡

    Nunca uses @Auditoria en la capa de Dominio.

    La anotación vive en application, pero el Aspecto que la procesa vive en infrastructure.

    Documenta siempre por qué una auditoría programática es necesaria para el negocio.

    Garantiza que la auditoría sea relevante, no solo "ruido técnico".