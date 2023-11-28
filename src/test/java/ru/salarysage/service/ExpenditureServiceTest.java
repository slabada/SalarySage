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
        when(expenditureRepository.existsByName(e.getName())).thenReturn(false);
        ExpenditureDTO r = expenditureService.create(e);
        verify(expenditureRepository, times(1)).save(e);
        assertThat(r).usingRecursiveComparison().isEqualTo(e);
    }

    @Test
    void get(){
        when(expenditureRepository.findById(e.getId())).thenReturn(Optional.ofNullable(e));
        Optional<ExpenditureDTO> r = expenditureService.get(e.getId());
        Assertions.assertTrue(r.isPresent());
        assertThat(r.get()).usingRecursiveComparison().isEqualTo(e);
    }

    @Test
    void put(){
        when(expenditureRepository.findById(e.getId())).thenReturn(Optional.ofNullable(e));
        when(expenditureRepository.existsByNameAndIdNot(newE.getName(),e.getId())).thenReturn(false);
        ExpenditureDTO r = expenditureService.put(e.getId(), newE);
        verify(expenditureRepository, times(1)).save(newE);
        assertThat(r).usingRecursiveComparison().isEqualTo(newE);
    }

    @Test
    void delete(){
        when(expenditureRepository.findById(e.getId())).thenReturn(Optional.ofNullable(e));
        expenditureService.delete(e.getId());
        verify(expenditureRepository, times(1)).deleteById(e.getId());
    }
}