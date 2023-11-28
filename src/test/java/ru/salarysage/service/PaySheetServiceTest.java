package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.dto.*;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PaySheetException;
import ru.salarysage.models.*;
import ru.salarysage.repository.PaySheetRepository;
import ru.salarysage.util.CalculationUtil;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaySheetServiceTest {
    @InjectMocks
    private PaySheetService paySheetService;
    @Mock
    private PaySheetRepository paySheetRepository;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private RateService rateService;
    @Mock
    private BenefitService benefitService;
    @Mock
    private TimeSheetService timeSheetService;
    @Mock
    private CalculationUtil calculationUtil;
    private PaySheetModel ps;
    private PaySheetDTO psDTO;
    private PaySheetModel newps;
    private PaySheetDTO newpsDTO;
    private TimeSheetModel t;
    private TimeSheetDTO tDTO;
    private PositionModel p;
    private PositionDTO pDTO;
    private EmployeeModel e;
    private EmployeeDTO eDTO;
    private BenefitModel b;
    private BenefitDTO bDTO;
    private RateModel r;
    private RateDTO rDTO;
    @BeforeEach
    void setUp() {
        p = new PositionModel();
        p.setId(1L);
        p.setName("Test");
        p.setRate(new BigDecimal(50000));

        pDTO = new PositionDTO();
        pDTO.setName("Test");
        pDTO.setRate(new BigDecimal(50000));

        e = new EmployeeModel();
        e.setId(1L);
        e.setLastName("Test");
        e.setFirstName("Test");
        e.setAddress("Test");
        e.setPosition(p);

        eDTO = new EmployeeDTO();
        eDTO.setLastName("Test");
        eDTO.setFirstName("Test");
        eDTO.setAddress("Test");
        eDTO.setPosition(pDTO);

        t = new TimeSheetModel();
        t.setId(1L);
        t.setEmployeeId(e);
        t.setDate(LocalDate.parse("2023-02-02"));
        t.setNotes("test");
        t.setHoliday(false);
        t.setHoursWorked(Time.valueOf("8:00:00"));

        tDTO = new TimeSheetDTO();
        tDTO.setEmployeeId(eDTO);
        tDTO.setDate(LocalDate.parse("2023-02-02"));
        tDTO.setNotes("test");
        tDTO.setHoliday(false);
        tDTO.setHoursWorked(Time.valueOf("8:00:00"));

        b = new BenefitModel();
        b.setId(1L);
        b.setName("Test");
        b.setAmount(new BigDecimal(1000));


        bDTO = new BenefitDTO();
        bDTO.setName("Test");
        bDTO.setAmount(new BigDecimal(1000));

        r = new RateModel();
        r.setId(1L);
        r.setName("Test");
        r.setPercent(13);

        rDTO = new RateDTO();
        rDTO.setName("Test");
        rDTO.setPercent(13);

        ps = new PaySheetModel();
        ps.setId(1L);
        ps.setBenefit(Collections.singleton(b));
        ps.setRate(Collections.singleton(r));
        ps.setEmployeeId(e);
        ps.setMonth(11);
        ps.setYear(2023);
        ps.setTotalAmount(new BigDecimal(36_666));

        psDTO = new PaySheetDTO();
        psDTO.setBenefit(Collections.singleton(bDTO));
        psDTO.setRate(Collections.singleton(rDTO));
        psDTO.setEmployeeId(eDTO);
        psDTO.setMonth(11);
        psDTO.setYear(2023);
        psDTO.setTotalAmount(new BigDecimal(36_666));

        newps = new PaySheetModel();
        newps.setId(1L);
        newps.setBenefit(Collections.singleton(b));
        newps.setRate(Collections.singleton(r));
        newps.setEmployeeId(e);
        newps.setMonth(11);
        newps.setYear(2023);
        newps.setTotalAmount(new BigDecimal(37_666));

        newpsDTO = new PaySheetDTO();
        newpsDTO.setBenefit(Collections.singleton(bDTO));
        newpsDTO.setRate(Collections.singleton(rDTO));
        newpsDTO.setEmployeeId(eDTO);
        newpsDTO.setMonth(11);
        newpsDTO.setYear(2023);
        newpsDTO.setTotalAmount(new BigDecimal(37_666));
    }

    @Test
    void paySheetNotFount(){
        when(employeeService.get(ps.getEmployeeId().getId())).thenReturn(Optional.of(eDTO));
        assertThrows(PaySheetException.PaySheetNotFount.class, () ->{
            paySheetService.create(ps);
            paySheetService.get(ps.getId());
            paySheetService.put(ps.getId(), ps);
            paySheetService.delete(ps.getId());
        });
    }

    @Test
    void employeeNotFoundException(){
        assertThrows(EmployeeException.EmployeeNotFoundException.class, () ->{
            paySheetService.create(ps);
            paySheetService.put(ps.getId(), ps);
            paySheetService.getAll(ps.getId());
        });
    }

    @Test
    void invalidIdException(){
        assertThrows(GeneraleException.InvalidIdException.class, () ->{
            paySheetService.get(-1L);
            paySheetService.put(-1L, ps);
            paySheetService.delete(-1L);
            paySheetService.getAll(-1L);
        });
    }

    @Test
    void create() {
        when(employeeService.get(ps.getEmployeeId().getId())).thenReturn(Optional.of(eDTO));
        when(timeSheetService.searchByYearAndMonth(
                ps.getEmployeeId().getId(),
                ps.getYear(),
                (byte) ps.getMonth()
        )).thenReturn(List.of(tDTO));
        when(benefitService.check(ps)).thenReturn(Collections.singleton(b));
        when(rateService.check(ps)).thenReturn(Collections.singleton(r));
        when(calculationUtil.calculationTotal(
                List.of(tDTO),
                ps.getEmployeeId().getPosition(),
                ps
        )).thenReturn(psDTO.getTotalAmount());
        PaySheetDTO rps = paySheetService.create(ps);
        assertThat(rps).usingRecursiveComparison().isEqualTo(psDTO);
        verify(paySheetRepository,times(1)).save(ps);
    }

    @Test
    void get(){
        when(paySheetRepository.findById(ps.getId())).thenReturn(Optional.of(ps));
        Optional<PaySheetDTO> rps = paySheetService.get(ps.getId());
        assertTrue(rps.isPresent());
        assertThat(rps.get()).usingRecursiveComparison().isEqualTo(ps);
    }

    @Test
    void put(){
        when(paySheetRepository.findById(ps.getId())).thenReturn(Optional.of(ps));
        when(employeeService.get(newps.getEmployeeId().getId())).thenReturn(Optional.of(eDTO));
        when(timeSheetService.searchByYearAndMonth(
                newps.getEmployeeId().getId(),
                newps.getYear(),
                (byte) newps.getMonth()
        )).thenReturn(List.of(tDTO));
        when(benefitService.check(newps)).thenReturn(Collections.singleton(b));
        when(rateService.check(newps)).thenReturn(Collections.singleton(r));
        when(calculationUtil.calculationTotal(
                List.of(tDTO),
                newps.getEmployeeId().getPosition(),
                newps
        )).thenReturn(newpsDTO.getTotalAmount());
        PaySheetDTO rps = paySheetService.put(ps.getId(), newps);
        assertThat(rps).usingRecursiveComparison().isEqualTo(newpsDTO);
        verify(paySheetRepository, times(1)).save(newps);
    }

    @Test
    void delete(){
        when(paySheetRepository.findById(ps.getId())).thenReturn(Optional.of(ps));
        paySheetService.delete(ps.getId());
        verify(paySheetRepository, times(1)).findById(ps.getId());
    }

    @Test
    void getByEmployeeId(){
        when(paySheetRepository.findAllByEmployeeId_Id(ps.getId())).thenReturn(List.of(ps));
        List<PaySheetDTO> rps = paySheetService.getAll(ps.getId());
        assertThat(rps).usingRecursiveComparison().isEqualTo(List.of(ps));
    }
}