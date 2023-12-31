package ru.salarysage.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.salarysage.event.CreateRateEvent;
import ru.salarysage.models.RateModel;
import ru.salarysage.repository.RateRepository;

@Component
@RequiredArgsConstructor
public class RateListener {

    protected final RateRepository rateRepository;

    @EventListener()
    public void createStartRate(CreateRateEvent event) {
        RateModel startRate = new RateModel(1, "НДФЛ", 13);
        boolean rDb = rateRepository.existsByName(startRate.getName());
        if (!rDb){
            rateRepository.save(startRate);
        }
    }
}
