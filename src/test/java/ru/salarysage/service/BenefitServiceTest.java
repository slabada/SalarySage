package ru.salarysage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.dto.BenefitDTO;
import ru.salarysage.exception.BenefitException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.BenefitModel;
import ru.salarysage.repository.BenefitRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BenefitServiceTest {
    @InjectMocks
    private BenefitService benefitService;
    @Mock
    private BenefitRepository benefitRepository;
    @Mock
    private GenericMapper genericMapper;
    private BenefitModel b;
    private BenefitDTO bDTO;
    private BenefitModel newb;

    @BeforeEach
    void setUp() {
        b = new BenefitModel();
        b.setId(1L);
        b.setName("Test");
        b.setAmount(new BigDecimal(1000));

        bDTO = new BenefitDTO();
        bDTO.setName("Test");
        bDTO.setAmount(new BigDecimal(1000));

        newb = new BenefitModel();
        newb.setId(1L);
        newb.setName("newTest");
        newb.setAmount(new BigDecimal(1001));
    }

    @Test
    void benefitAlreadyExistsException(){

        when(benefitRepository.existsByName(b.getName())).thenReturn(true);

        assertThrows(BenefitException.BenefitAlreadyExistsException.class, () -> {
            benefitService.create(b);
            benefitService.put(b.getId(), newb);
        });
    }

    @Test
    public void nullBenefitException() {

        when(benefitRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BenefitException.NullBenefitException.class, () -> {
            benefitService.get(1L);
            benefitService.put(1L, newb);
            benefitService.delete(1L);
        });
    }

    @Test
    public void invalidIdException() {

        assertThrows(GeneraleException.InvalidIdException.class, () -> {
            benefitService.get(-1L);
            benefitService.put(-1L, b);
            benefitService.delete(-1L);
        });
    }

    @Test
    void create() {
        when(benefitRepository.existsByName(anyString())).thenReturn(false);
        when(benefitRepository.save(any(BenefitModel.class))).thenReturn(b);
        when(genericMapper.convertToDto(any(BenefitModel.class), eq(BenefitDTO.class))).thenReturn(bDTO);
        BenefitDTO result = benefitService.create(b);
        assertEquals(bDTO, result);
        verify(benefitRepository).existsByName(b.getName());
        verify(benefitRepository).save(b);
        verify(genericMapper).convertToDto(b, BenefitDTO.class);
    }

    @Test
    void get(){
        when(benefitRepository.findById(b.getId())).thenReturn(Optional.of(b));
        when(genericMapper.convertToDto(Optional.of(b), BenefitDTO.class)).thenReturn(bDTO);
        Optional<BenefitDTO> result = benefitService.get(b.getId());
        assertEquals(Optional.of(bDTO), result);
        verify(benefitRepository).findById(b.getId());
        verify(genericMapper).convertToDto(Optional.of(b), BenefitDTO.class);
    }

    @Test
    void put(){
        when(benefitRepository.findById(b.getId())).thenReturn(Optional.of(b));
        when(benefitRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(benefitRepository.save(any(BenefitModel.class))).thenReturn(newb);
        when(genericMapper.convertToDto(newb, BenefitDTO.class)).thenReturn(bDTO);
        BenefitDTO result = benefitService.put(b.getId(), newb);
        assertEquals(bDTO, result);
        verify(benefitRepository).findById(b.getId());
        verify(benefitRepository).existsByNameAndIdNot(newb.getName(), b.getId());
        verify(benefitRepository).save(newb);
        verify(genericMapper).convertToDto(newb, BenefitDTO.class);
    }

    @Test
    void delete(){
        when(benefitRepository.findById(b.getId())).thenReturn(Optional.of(b));
        benefitService.delete(b.getId());
        verify(benefitRepository, times(1)).deleteById(b.getId());
    }
}