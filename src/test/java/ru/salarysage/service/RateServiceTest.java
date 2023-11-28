package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.dto.RateDTO;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.RateException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.RateModel;
import ru.salarysage.repository.RateRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {
    @InjectMocks
    private RateService rateService;

    @Mock
    private RateRepository rateRepository;
    @Mock
    private GenericMapper genericMapper;
    private RateModel r;

    private RateDTO rDTO;

    private RateModel newr;

    @BeforeEach
    void setUp() {
        r = new RateModel();
        r.setId(1L);
        r.setName("Test");
        r.setPercent(13);

        rDTO = new RateDTO();
        rDTO.setName("Test");
        rDTO.setPercent(13);

        newr = new RateModel();
        newr.setId(1L);
        newr.setName("newTest");
        newr.setPercent(15);
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
        when(rateRepository.existsByName(anyString())).thenReturn(false);
        when(rateRepository.save(any(RateModel.class))).thenReturn(r);
        when(genericMapper.convertToDto(any(RateModel.class), eq(RateDTO.class))).thenReturn(rDTO);
        RateDTO result = rateService.create(r);
        assertEquals(rDTO, result);
        verify(rateRepository).existsByName(r.getName());
        verify(rateRepository).save(r);
        verify(genericMapper).convertToDto(r, RateDTO.class);
    }

    @Test
    void get(){
        when(rateRepository.findById(r.getId())).thenReturn(Optional.of(r));
        when(genericMapper.convertToDto(Optional.of(r), RateDTO.class)).thenReturn(rDTO);
        Optional<RateDTO> result = rateService.get(r.getId());
        assertEquals(Optional.of(rDTO), result);
        verify(rateRepository).findById(r.getId());
        verify(genericMapper).convertToDto(Optional.of(r), RateDTO.class);
    }

    @Test
    void put(){
        when(rateRepository.findById(r.getId())).thenReturn(Optional.of(newr));
        when(rateRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(rateRepository.save(any(RateModel.class))).thenReturn(newr);
        when(genericMapper.convertToDto(newr, RateDTO.class)).thenReturn(rDTO);
        RateDTO result = rateService.put(r.getId(), newr);
        assertEquals(rDTO, result);
        verify(rateRepository).findById(r.getId());
        verify(rateRepository).existsByNameAndIdNot(newr.getName(), r.getId());
        verify(rateRepository).save(newr);
        verify(genericMapper).convertToDto(newr, RateDTO.class);
    }

    @Test
    void delete(){
        when(rateRepository.findById(r.getId())).thenReturn(Optional.of(r));
        rateService.delete(r.getId());
        verify(rateRepository, times(1)).deleteById(r.getId());
    }
}