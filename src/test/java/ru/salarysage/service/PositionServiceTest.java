package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.models.PositionModel;
import ru.salarysage.repository.PositionRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @InjectMocks
    private PositionService positionService;
    @Mock
    private PositionRepository positionRepository;

    private PositionModel p;

    private PositionModel newp;

    @BeforeEach
    void setUp() {

        p = new PositionModel();

        p.setId(1L);
        p.setName("Test");
        p.setRate(new BigDecimal(50000));

        newp = new PositionModel();

        newp.setId(1L);
        newp.setName("newTest");
        newp.setRate(new BigDecimal(55000));
    }

    @Test
    void positionAlreadyExistsException(){

        when(positionRepository.existsByName(p.getName())).thenReturn(true);

        assertThrows(PositionException.PositionAlreadyExistsException.class, () -> {
            positionService.create(p);
            positionService.put(p.getId(), p);
        });
    }

    @Test
    public void invalidIdException() {

        assertThrows(GeneraleException.InvalidIdException.class, () -> {
            positionService.get(-1L);
            positionService.put(-1L, p);
            positionService.delete(-1L);
        });
    }

    @Test
    public void positionNotFoundException() {

        when(positionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(PositionException.PositionNotFoundException.class, () -> {
            positionService.get(p.getId());
        });
    }

    @Test
    void create() {

        when(positionRepository.existsByName(p.getName())).thenReturn(false);

        positionService.create(p);

        verify(positionRepository, times(1)).save(p);
    }

    @Test
    void get(){

        when(positionRepository.findById(p.getId())).thenReturn(Optional.of(p));

        Optional<PositionModel> rp = positionService.get(p.getId());

        assertTrue(rp.isPresent());
        assertEquals(p, rp.get());
    }

    @Test
    void put(){

        when(positionRepository.findById(p.getId())).thenReturn(Optional.of(p));

        when(positionRepository.existsByNameAndIdNot(newp.getName(), p.getId())).thenReturn(false);

        PositionModel rp = positionService.put(p.getId(), newp);

        assertEquals(newp, rp);

        verify(positionRepository, times(1)).save(newp);
    }

    @Test
    void delete(){

        when(positionRepository.findById(p.getId())).thenReturn(Optional.of(p));

        positionService.delete(p.getId());

        verify(positionRepository, times(1)).deleteById(p.getId());
    }
}