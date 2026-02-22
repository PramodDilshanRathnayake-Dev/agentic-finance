package com.agenticfinance.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.Map;

/**
 * Event envelope per EVENTS-AND-MCP-v1.0.0.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventEnvelope {

    private String eventId;
    private String eventType;
    private Instant timestamp;
    private String source;
    private Map<String, Object> payload;

    public static EventEnvelope of(String eventType, String source, Map<String, Object> payload) {
        return EventEnvelope.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .eventType(eventType)
                .timestamp(Instant.now())
                .source(source)
                .payload(payload)
                .build();
    }
}
