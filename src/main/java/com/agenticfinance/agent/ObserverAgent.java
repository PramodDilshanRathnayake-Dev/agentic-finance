package com.agenticfinance.agent;

import com.agenticfinance.domain.AuditLog;
import com.agenticfinance.event.EventPublisher;
import com.agenticfinance.event.EventTypes;
import com.agenticfinance.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

/**
 * Observer agent: log reasoning, hallucination alerts. FRS §4.5.
 */
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
        eventPublisher.publish(EventTypes.AGENT_REASONING, SOURCE, Map.of(
                "agentId", agentId,
                "trace", trace != null ? trace : "",
                "promptRef", promptRef != null ? promptRef : ""
        ));
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
        eventPublisher.publish(EventTypes.HALLUCINATION_ALERT, SOURCE, Map.of(
                "agentId", agentId,
                "alertType", alertType != null ? alertType : "",
                "suggestion", suggestion != null ? suggestion : ""
        ));
    }

    @EventListener
    public void onAppEvent(com.agenticfinance.event.AppEvent event) {
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
