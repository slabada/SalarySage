package ru.salarysage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.dto.ExpenditureDTO;
import ru.salarysage.exception.ExpenditureException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.repository.ExpenditureRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenditureServiceTest {
    @InjectMocks
    private ExpenditureService expenditureService;
    @Mock
    private ExpenditureRepository expenditureRepository;
    @Mock
    private GenericMapper genericMapper;
    private ExpenditureModel e;
    private ExpenditureDTO eDTO;
    private ExpenditureModel newE;
    @BeforeEach
    void setUp() {
        e = new ExpenditureModel();
        e.setId(1L);
        e.setName("Test");
        e.setAmount(BigDecimal.valueOf(6666));

        eDTO = new ExpenditureDTO();
        eDTO.setName("Test");
        eDTO.setAmount(BigDecimal.valueOf(6666));

        newE = new ExpenditureModel();
        newE.setId(1L);
        newE.setName("newTest");
        newE.setAmount(BigDecimal.valueOf(7777));
    }

    @Test
    void expenditureConflictName(){
        when(expenditureRepository.existsByName(e.getName())).thenReturn(true);
        assertThrows(ExpenditureException.ConflictName.class, () -> {
            expenditureService.create(e);
            expenditureService.put(1L, newE);
        });
    }

    @Test
    public void invalidIdException() {
        assertThrows(GeneraleException.InvalidIdException.class, () -> {
            expenditureService.get(-1L);
            expenditureService.put(-1L, newE);
            expenditureService.delete(-1L);
        });
    }

    @Test
    void noExpenditure(){
        when(expenditureRepository.findById(e.getId())).thenReturn(Optional.empty());
        assertThrows(ExpenditureException.NoExpenditure.class, () -> {
            expenditureService.get(1L);
            expenditureService.put(1L, newE);
            expenditureService.delete(1L);
        });
    }

    @Test
    void create() {
        when(expenditureRepository.existsByName(anyString())).thenReturn(false);
        when(expenditureRepository.save(any(ExpenditureModel.class))).thenReturn(e);
        when(genericMapper.convertToDto(any(ExpenditureModel.class), eq(ExpenditureDTO.class))).thenReturn(eDTO);
        ExpenditureDTO result = expenditureService.create(e);
        assertEquals(eDTO, result);
        verify(expenditureRepository).existsByName(e.getName());
        verify(expenditureRepository).save(e);
        verify(genericMapper).convertToDto(e, ExpenditureDTO.class);
    }

    @Test
    void get(){
        when(expenditureRepository.findById(e.getId())).thenReturn(Optional.of(e));
        when(genericMapper.convertToDto(Optional.of(e), ExpenditureDTO.class)).thenReturn(eDTO);
        Optional<ExpenditureDTO> result = expenditureService.get(e.getId());
        assertEquals(Optional.of(eDTO), result);
        verify(expenditureRepository).findById(e.getId());
        verify(genericMapper).convertToDto(Optional.of(e), ExpenditureDTO.class);
    }

    @Test
    void put(){
        when(expenditureRepository.findById(e.getId())).thenReturn(Optional.of(e));
        when(expenditureRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(expenditureRepository.save(any(ExpenditureModel.class))).thenReturn(newE);
        when(genericMapper.convertToDto(newE, ExpenditureDTO.class)).thenReturn(eDTO);
        ExpenditureDTO result = expenditureService.put(e.getId(), newE);
        assertEquals(eDTO, result);
        verify(expenditureRepository).findById(e.getId());
        verify(expenditureRepository).existsByNameAndIdNot(newE.getName(), e.getId());
        verify(expenditureRepository).save(newE);
        verify(genericMapper).convertToDto(newE, ExpenditureDTO.class);
    }

    @Test
    void delete(){
        when(expenditureRepository.findById(e.getId())).thenReturn(Optional.ofNullable(e));
        expenditureService.delete(e.getId());
        verify(expenditureRepository, times(1)).deleteById(e.getId());
    }
}