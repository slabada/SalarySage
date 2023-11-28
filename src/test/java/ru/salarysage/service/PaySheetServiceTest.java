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
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.*;
import ru.salarysage.repository.EmployeeRepository;
import ru.salarysage.repository.PaySheetRepository;
import ru.salarysage.util.CalculationUtil;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaySheetServiceTest {
    @InjectMocks
    private PaySheetService paySheetService;
    @Mock
    private PaySheetRepository paySheetRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RateService rateService;
    @Mock
    private BenefitService benefitService;
    @Mock
    private TimeSheetService timeSheetService;
    @Mock
    private CalculationUtil calculationUtil;
    @Mock
    private GenericMapper genericMapper;
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
        when(employeeRepository.findById(ps.getEmployeeId().getId())).thenReturn(Optional.of(e));
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
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(e));
        when(timeSheetService.searchByYearAndMonth(anyLong(), anyInt(), anyByte())).thenReturn(Collections.singletonList(tDTO));
        when(benefitService.check(any(PaySheetModel.class))).thenReturn(Collections.singleton(b));
        when(rateService.check(any(PaySheetModel.class))).thenReturn(Collections.singleton(r));
        when(calculationUtil.calculationTotal(anyList(), any(PositionModel.class), any(PaySheetModel.class))).thenReturn(new BigDecimal("100"));
        when(paySheetRepository.save(any(PaySheetModel.class))).thenReturn(ps);
        when(genericMapper.convertToDto(ps, PaySheetDTO.class)).thenReturn(psDTO);
        PaySheetDTO result = paySheetService.create(ps);
        assertEquals(psDTO, result);
        verify(employeeRepository).findById(e.getId());
        verify(timeSheetService).searchByYearAndMonth(e.getId(), ps.getYear(), (byte) ps.getMonth());
        verify(benefitService).check(ps);
        verify(rateService).check(ps);
        verify(calculationUtil).calculationTotal(Collections.singletonList(tDTO), e.getPosition(), ps);
        verify(paySheetRepository).save(ps);
        verify(genericMapper).convertToDto(ps, PaySheetDTO.class);
    }

    @Test
    void get(){
        when(paySheetRepository.findById(ps.getId())).thenReturn(Optional.of(ps));
        when(genericMapper.convertToDto(Optional.of(ps), PaySheetDTO.class)).thenReturn(psDTO);
        Optional<PaySheetDTO> result = paySheetService.get(ps.getId());
        assertEquals(Optional.of(psDTO), result);
        verify(paySheetRepository).findById(ps.getId());
        verify(genericMapper).convertToDto(Optional.of(ps), PaySheetDTO.class);
    }

    @Test
    void put(){
        when(paySheetRepository.findById(ps.getId())).thenReturn(Optional.of(ps));
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(e));
        when(timeSheetService.searchByYearAndMonth(anyLong(), anyInt(), anyByte())).thenReturn(Collections.singletonList(tDTO));
        when(benefitService.check(any(PaySheetModel.class))).thenReturn(Collections.singleton(b));
        when(rateService.check(any(PaySheetModel.class))).thenReturn(Collections.singleton(r));
        when(calculationUtil.calculationTotal(anyList(), any(PositionModel.class), any(PaySheetModel.class))).thenReturn(new BigDecimal("100.00"));
        when(paySheetRepository.save(any(PaySheetModel.class))).thenReturn(newps);
        when(genericMapper.convertToDto(newps, PaySheetDTO.class)).thenReturn(psDTO);
        PaySheetDTO result = paySheetService.put(ps.getId(), newps);
        assertEquals(psDTO, result);
        verify(paySheetRepository).findById(ps.getId());
        verify(employeeRepository).findById(e.getId());
        verify(timeSheetService).searchByYearAndMonth(e.getId(), newps.getYear(), (byte) newps.getMonth());
        verify(benefitService).check(newps);
        verify(rateService).check(newps);
        verify(calculationUtil).calculationTotal(Collections.singletonList(tDTO), e.getPosition(), newps);
        verify(paySheetRepository).save(newps);
        verify(genericMapper).convertToDto(newps, PaySheetDTO.class);
    }

    @Test
    void delete(){
        when(paySheetRepository.findById(ps.getId())).thenReturn(Optional.of(ps));
        paySheetService.delete(ps.getId());
        verify(paySheetRepository, times(1)).findById(ps.getId());
    }

    @Test
    void getAll(){
        when(paySheetRepository.findAllByEmployeeId_Id(ps.getId())).thenReturn(Collections.singletonList(ps));
        when(genericMapper.convertToDto(any(PaySheetModel.class), eq(PaySheetDTO.class))).thenReturn(psDTO);
        List<PaySheetDTO> result = paySheetService.getAll(ps.getId());
        assertEquals(List.of(psDTO), result);
        verify(paySheetRepository).findAllByEmployeeId_Id(ps.getId());
        verify(genericMapper).convertToDto(ps, PaySheetDTO.class);
    }
}