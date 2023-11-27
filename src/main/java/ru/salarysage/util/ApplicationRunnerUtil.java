package ru.salarysage.util;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.salarysage.event.CreateRateEvent;

// Event который запускает каждый раз при запуске приложения для того, чтобы
// дернуть метод createStartRate в RateService, который создает первоначальный налог "НДФЛ"
@Component
public class ApplicationRunnerUtil implements ApplicationRunner {
    public final ApplicationEventPublisher eventPublisher;

    public ApplicationRunnerUtil(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void run(ApplicationArguments args){
        // Публикуем event для слушателей
        eventPublisher.publishEvent(new CreateRateEvent(this));
    }
}
