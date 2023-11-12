package ru.salarysage.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.salarysage.exception.EmployeeException;
import ru.salarysage.salarysage.exception.GeneraleException;
import ru.salarysage.salarysage.exception.PaySheetException;
import ru.salarysage.salarysage.models.*;
import ru.salarysage.salarysage.repository.PaySheetRepository;
import ru.salarysage.salarysage.util.CalculationUtil;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    private PaySheetModel newps;

    private TimeSheetModel t;

    private PositionModel p;

    private EmployeeModel e;

    private BenefitModel b;

    private RateModel r;

    @BeforeEach
    void setUp() {

        p = new PositionModel();

        p.setId(1L);
        p.setName("Test");
        p.setRate(new BigDecimal(50000));

        e = new EmployeeModel();

        e.setId(1L);
        e.setLastName("Test");
        e.setFirstName("Test");
        e.setAddress("Test");
        e.setPosition(p);

        t = new TimeSheetModel();

        t.setId(1L);
        t.setEmployeeId(e);
        t.setDate(LocalDate.parse("2023-02-02"));
        t.setNotes("test");
        t.setHoliday(false);
        t.setHoursWorked(Time.valueOf("8:00:00"));

        b = new BenefitModel();

        b.setId(1L);
        b.setName("Test");
        b.setAmount(new BigDecimal(1000));

        r = new RateModel();

        r.setId(1L);
        r.setName("Test");
        r.setPercent(13);

        ps = new PaySheetModel();

        ps.setId(1L);
        ps.setBenefit(Collections.singleton(b));
        ps.setRate(Collections.singleton(r));
        ps.setEmployeeId(e);
        ps.setDate(YearMonth.parse("2023-02"));
        ps.setTotalAmount(new BigDecimal(36_666));

        newps = new PaySheetModel();

        newps.setId(1L);
        newps.setBenefit(Collections.singleton(b));
        newps.setRate(Collections.singleton(r));
        newps.setEmployeeId(e);
        newps.setDate(YearMonth.parse("2023-03"));
        newps.setTotalAmount(new BigDecimal(37_666));
    }

    @Test
    void paySheetNotFount(){

        when(employeeService.get(ps.getEmployeeId().getId())).thenReturn(Optional.of(e));

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
            paySheetService.getByEmployeeId(ps.getId());
        });
    }

    @Test
    void invalidIdException(){

        assertThrows(GeneraleException.InvalidIdException.class, () ->{
            paySheetService.get(-1L);
            paySheetService.put(-1L, ps);
            paySheetService.delete(-1L);
            paySheetService.getByEmployeeId(-1L);
        });
    }

    @Test
    void create() {

        when(employeeService.get(ps.getEmployeeId().getId())).thenReturn(Optional.of(e));

        when(timeSheetService.searchByYearAndMonth(
                ps.getEmployeeId().getId(),
                ps.getDate().getYear(),
                (byte) ps.getDate().getMonth().getValue()
        )).thenReturn(List.of(t));

        when(benefitService.check(ps)).thenReturn(Collections.singleton(b));

        when(rateService.check(ps)).thenReturn(Collections.singleton(r));

        when(calculationUtil.calculationTotal(
                List.of(t),
                ps.getEmployeeId().getPosition(),
                ps
        )).thenReturn(new BigDecimal(anyLong()));

        PaySheetModel rps = paySheetService.create(ps);

        assertEquals(ps, rps);

        verify(paySheetRepository,times(1)).save(ps);
    }

    @Test
    void get(){

        when(paySheetRepository.findById(ps.getId())).thenReturn(Optional.of(ps));

        Optional<PaySheetModel> rps = paySheetService.get(ps.getId());

        assertTrue(rps.isPresent());
        assertEquals(ps, rps.get());
    }

    @Test
    void put(){

        when(paySheetRepository.findById(ps.getId())).thenReturn(Optional.of(ps));

        when(employeeService.get(newps.getEmployeeId().getId())).thenReturn(Optional.of(e));

        when(timeSheetService.searchByYearAndMonth(
                newps.getEmployeeId().getId(),
                newps.getDate().getYear(),
                (byte) newps.getDate().getMonth().getValue()
        )).thenReturn(List.of(t));

        when(benefitService.check(newps)).thenReturn(Collections.singleton(b));

        when(rateService.check(newps)).thenReturn(Collections.singleton(r));

        when(calculationUtil.calculationTotal(
                List.of(t),
                newps.getEmployeeId().getPosition(),
                newps
        )).thenReturn(new BigDecimal(anyLong()));

        paySheetService.put(ps.getId(), newps);

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

        List<PaySheetModel> rps = paySheetService.getByEmployeeId(ps.getId());

        assertEquals(List.of(ps), rps);
    }
}