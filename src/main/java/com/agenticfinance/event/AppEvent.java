package com.agenticfinance.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppEvent extends ApplicationEvent {

    private final EventEnvelope envelope;

    public AppEvent(Object source, EventEnvelope envelope) {
        super(source);
        this.envelope = envelope;
    }
}
