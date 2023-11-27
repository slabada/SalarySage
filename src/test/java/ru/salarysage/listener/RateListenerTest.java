package ru.salarysage.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.event.CreateRateEvent;
import ru.salarysage.models.RateModel;
import ru.salarysage.repository.RateRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateListenerTest {
    @InjectMocks
    private RateListener rateListener;
    @Mock
    private RateRepository rateRepository;
    CreateRateEvent event;

    @Test
    void createStartRate(){
        RateModel StartRate = new RateModel(1, "НДФЛ",13);
        when(rateRepository.existsByName(StartRate.getName())).thenReturn(false);
        rateListener.createStartRate(event);
        verify(rateRepository, times(1)).save(any(RateModel.class));
    }
}