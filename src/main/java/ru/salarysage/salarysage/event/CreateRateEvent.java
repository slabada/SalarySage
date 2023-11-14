package ru.salarysage.salarysage.event;

import org.springframework.context.ApplicationEvent;

// Сам event

public class CreateRateEvent extends ApplicationEvent {
    public CreateRateEvent(Object source) {
        super(source);
    }
}
