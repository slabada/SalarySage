package ru.salarysage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.exception.BenefitException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.models.BenefitModel;
import ru.salarysage.repository.BenefitRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BenefitServiceTest {

    @InjectMocks
    private BenefitService benefitService;

    @Mock
    private BenefitRepository benefitRepository;

    private BenefitModel b;

    private BenefitModel newb;

    @BeforeEach
    void setUp() {

        b = new BenefitModel();

        b.setId(1L);
        b.setName("Test");
        b.setAmount(new BigDecimal(1000));

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

        when(benefitRepository.existsByName(b.getName())).thenReturn(false);

        benefitService.create(b);

        verify(benefitRepository, times(1)).save(b);
    }

    @Test
    void get(){

        when(benefitRepository.findById(b.getId())).thenReturn(Optional.of(b));

        Optional<BenefitModel> rb = benefitService.get(b.getId());

        Assertions.assertTrue(rb.isPresent());
        assertEquals(b, rb.get());
    }

    @Test
    void put(){

        when(benefitRepository.findById(b.getId())).thenReturn(Optional.of(b));

        when(benefitRepository.existsByNameAndIdNot(newb.getName(), b.getId())).thenReturn(false);

        BenefitModel rb = benefitService.put(b.getId(), newb);

        assertEquals(newb, rb);

        verify(benefitRepository, times(1)).save(newb);

    }

    @Test
    void delete(){

        when(benefitRepository.findById(b.getId())).thenReturn(Optional.of(b));

        benefitService.delete(b.getId());

        verify(benefitRepository, times(1)).deleteById(b.getId());
    }
}