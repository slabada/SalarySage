package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.PositionModel;
import ru.salarysage.repository.PositionRepository;
import static org.assertj.core.api.Assertions.assertThat;

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
    @Mock
    private GenericMapper genericMapper;
    private PositionModel p;
    private PositionDTO pDTO;
    private PositionModel newp;
    private PositionDTO newpDTO;
    @BeforeEach
    void setUp() {
        p = new PositionModel();
        p.setId(1L);
        p.setName("Test");
        p.setRate(new BigDecimal(50000));

        pDTO = new PositionDTO();
        pDTO.setName("Test");
        pDTO.setRate(new BigDecimal(50000));

        newp = new PositionModel();
        newp.setId(1L);
        newp.setName("newTest");
        newp.setRate(new BigDecimal(55000));

        newpDTO = new PositionDTO();
        newpDTO.setName("newTest");
        newpDTO.setRate(new BigDecimal(55000));
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
        when(positionRepository.existsByName(anyString())).thenReturn(false);
        when(positionRepository.save(any(PositionModel.class))).thenReturn(p);
        when(genericMapper.convertToDto(any(PositionModel.class), eq(PositionDTO.class))).thenReturn(pDTO);
        PositionDTO result = positionService.create(p);
        assertEquals(pDTO, result);
        verify(positionRepository).existsByName(p.getName());
        verify(positionRepository).save(p);
        verify(genericMapper).convertToDto(p, PositionDTO.class);
    }

    @Test
    void get(){
        when(positionRepository.findById(p.getId())).thenReturn(Optional.of(p));
        when(genericMapper.convertToDto(eq(Optional.of(p)), eq(PositionDTO.class))).thenReturn(pDTO);
        Optional<PositionDTO> rp = positionService.get(p.getId());
        assertTrue(rp.isPresent());
        assertThat(rp.get()).usingRecursiveComparison().isEqualTo(pDTO);
    }

    @Test
    void put(){
        when(positionRepository.findById(p.getId())).thenReturn(Optional.of(p));
        when(genericMapper.convertToDto(Optional.of(p), PositionDTO.class)).thenReturn(pDTO);
        Optional<PositionDTO> result = positionService.get(p.getId());
        assertEquals(Optional.of(pDTO), result);
        verify(positionRepository).findById(p.getId());
        verify(genericMapper).convertToDto(Optional.of(p), PositionDTO.class);
    }

    @Test
    void delete(){
        when(positionRepository.findById(p.getId())).thenReturn(Optional.of(p));
        positionService.delete(p.getId());
        verify(positionRepository, times(1)).deleteById(p.getId());
    }
}