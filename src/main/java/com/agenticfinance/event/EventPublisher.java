package com.agenticfinance.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(String eventType, String source, Map<String, Object> payload) {
        EventEnvelope envelope = EventEnvelope.of(eventType, source, payload);
        applicationEventPublisher.publishEvent(new AppEvent(this, envelope));
        log.debug("Published event: {} from {}", eventType, source);
    }
}
