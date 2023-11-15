package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.RateException;
import ru.salarysage.models.RateModel;
import ru.salarysage.repository.RateRepository;
import ru.salarysage.event.CreateRateEvent;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {
    @InjectMocks
    private RateService rateService;

    @Mock
    private RateRepository rateRepository;

    private RateModel r;

    private RateModel newr;

    CreateRateEvent event;

    @BeforeEach
    void setUp() {

        r = new RateModel();

        r.setId(1L);
        r.setName("Test");
        r.setPercent(13);

        newr = new RateModel();

        newr.setId(1L);
        newr.setName("newTest");
        newr.setPercent(15);
    }

    @Test
    void createStartRate(){

        RateModel StartRate = new RateModel(1, "НДФЛ",13);

        when(rateRepository.existsByName(StartRate.getName())).thenReturn(false);

        rateService.createStartRate(event);

        verify(rateRepository, times(1)).save(any(RateModel.class));
    }

    @Test
    void rateAlreadyExistsException(){

        when(rateRepository.existsByName(r.getName())).thenReturn(true);

        assertThrows(RateException.RateAlreadyExistsException.class, () -> {
            rateService.create(r);
            rateService.put(1L, newr);
        });
    }

    @Test
    public void nullRateException() {

        when(rateRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RateException.NullRateException.class, () -> {
            rateService.get(1L);
            rateService.put(1L, newr);
            rateService.delete(1L);
        });
    }

    @Test
    public void invalidIdException() {

        assertThrows(GeneraleException.InvalidIdException.class, () -> {
            rateService.get(-1L);
            rateService.put(-1L,newr);
            rateService.delete(-1L);
        });
    }

    @Test
    void create() {

        when(rateRepository.existsByName(r.getName())).thenReturn(false);

        rateService.create(r);

        verify(rateRepository, times(1)).save(r);
    }

    @Test
    void get(){

        when(rateRepository.findById(r.getId())).thenReturn(Optional.of(r));

        Optional<RateModel> rr = rateService.get(r.getId());

        assertTrue(rr.isPresent());
        assertEquals(r, rr.get());
    }

    @Test
    void put(){

        when(rateRepository.findById(r.getId())).thenReturn(Optional.of(r));

        when(rateRepository.existsByNameAndIdNot(newr.getName(), r.getId())).thenReturn(false);

        RateModel rr = rateService.put(r.getId(),newr);

        assertEquals(newr,rr);

        verify(rateRepository, times(1)).save(newr);
    }

    @Test
    void delete(){

        when(rateRepository.findById(r.getId())).thenReturn(Optional.of(r));

        rateService.delete(r.getId());

        verify(rateRepository, times(1)).deleteById(r.getId());
    }
}