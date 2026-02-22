package com.agenticfinance.agent;

import com.agenticfinance.domain.AuditLog;
import com.agenticfinance.event.AppEvent;
import com.agenticfinance.event.EventPublisher;
import com.agenticfinance.event.EventTypes;
import com.agenticfinance.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObserverAgent {

    private static final String SOURCE = "observer-agent";

    private final AuditLogRepository auditLogRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void logReasoning(String agentId, String trace, String promptRef) {
        AuditLog logEntry = AuditLog.builder()
                .agentId(agentId)
                .eventType(EventTypes.AGENT_REASONING)
                .payload(trace != null ? trace : "")
                .timestamp(Instant.now())
                .build();
        auditLogRepository.save(logEntry);
    }

    @Transactional
    public void emitHallucinationAlert(String agentId, String alertType, String suggestion) {
        AuditLog logEntry = AuditLog.builder()
                .agentId(agentId)
                .eventType(EventTypes.HALLUCINATION_ALERT)
                .payload(String.format("type=%s suggestion=%s", alertType, suggestion))
                .timestamp(Instant.now())
                .build();
        auditLogRepository.save(logEntry);
    }

    @EventListener
    public void onAppEvent(AppEvent event) {
        String type = event.getEnvelope().getEventType();
        if (EventTypes.ORDER_PLACED.equals(type) || EventTypes.WITHDRAWAL_REQUESTED.equals(type)) {
            AuditLog logEntry = AuditLog.builder()
                    .agentId(event.getEnvelope().getSource())
                    .eventType(type)
                    .payload(event.getEnvelope().getPayload() != null ? event.getEnvelope().getPayload().toString() : "")
                    .timestamp(event.getEnvelope().getTimestamp())
                    .build();
            auditLogRepository.save(logEntry);
        }
    }
}
