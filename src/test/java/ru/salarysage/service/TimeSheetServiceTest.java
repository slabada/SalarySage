package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.dto.TimeSheetDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.TimeSheetException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.PositionModel;
import ru.salarysage.models.TimeSheetModel;
import ru.salarysage.repository.EmployeeRepository;
import ru.salarysage.repository.TimeSheetRepository;

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
class TimeSheetServiceTest {
    @InjectMocks
    private TimeSheetService timeSheetService;
    @Mock
    private TimeSheetRepository timeSheetRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private GenericMapper genericMapper;
    private TimeSheetModel t;
    private TimeSheetDTO tDTO;
    private TimeSheetModel newt;
    private TimeSheetDTO newtDTO;
    private PositionModel p;
    private PositionDTO pDTO;
    private EmployeeModel e;
    private EmployeeDTO eDTO;
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

        newt = new TimeSheetModel();
        newt.setId(1L);
        newt.setEmployeeId(e);
        newt.setDate(LocalDate.parse("2023-02-02"));
        newt.setNotes("test");
        newt.setHoliday(false);
        newt.setHoursWorked(Time.valueOf("8:00:00"));

        newtDTO = new TimeSheetDTO();
        newtDTO.setEmployeeId(eDTO);
        newtDTO.setDate(LocalDate.parse("2023-02-02"));
        newtDTO.setNotes("test");
        newtDTO.setHoliday(false);
        newtDTO.setHoursWorked(Time.valueOf("8:00:00"));
    }

    @Test
    void dateException(){
        when(timeSheetRepository.existsByDateAndEmployeeId(t.getDate(), t.getEmployeeId())).thenReturn(true);
        assertThrows(TimeSheetException.DateException.class, () ->{
            timeSheetService.create(t);
            timeSheetService.put(t.getId(), t);
        });
    }

    @Test
    void employeeNotFoundException(){
        assertThrows(EmployeeException.EmployeeNotFoundException.class, () ->{
            timeSheetService.create(t);
            timeSheetService.put(t.getId(), t);
            timeSheetService.searchByYearAndMonth(t.getId(), 2023, (byte) 2);
        });
    }

    @Test
    void nullTimeSheetException(){
        when(timeSheetRepository.findById(t.getId())).thenReturn(Optional.empty());
        assertThrows(TimeSheetException.NullTimeSheetException.class, () ->{
            timeSheetService.get(t.getId());
            timeSheetService.put(t.getId(), t);
            timeSheetService.delete(t.getId());
            timeSheetService.searchByYearAndMonth(t.getId(), 2023, (byte) 2);
        });
    }

    @Test
    void invalidIdException(){
        assertThrows(GeneraleException.InvalidIdException.class, () ->{
            timeSheetService.get(-1L);
            timeSheetService.put(-1L, t);
            timeSheetService.delete(-1L);
            timeSheetService.searchByYearAndMonth(-1L, 2023, (byte) 2);
        });
    }

    @Test
    void create() {
        when(timeSheetRepository.existsByDateAndEmployeeId(any(LocalDate.class), any(EmployeeModel.class))).thenReturn(false);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(e));
        when(timeSheetRepository.save(any(TimeSheetModel.class))).thenReturn(t);
        when(genericMapper.convertToDto(any(TimeSheetModel.class), eq(TimeSheetDTO.class))).thenReturn(tDTO);

        // Вызываем тестируемый метод
        TimeSheetDTO result = timeSheetService.create(t);

        // Проверяем результат
        assertEquals(tDTO, result);

        // Проверяем, что методы были вызваны с правильными аргументами
        verify(timeSheetRepository).existsByDateAndEmployeeId(t.getDate(), t.getEmployeeId());
        verify(employeeRepository).findById(e.getId());
        verify(timeSheetRepository).save(t);
        verify(genericMapper).convertToDto(t, TimeSheetDTO.class);
    }

    @Test
    void get(){
        when(timeSheetRepository.findById(t.getId())).thenReturn(Optional.of(t));
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(e));
        when(genericMapper.convertToDto(Optional.of(t), TimeSheetDTO.class)).thenReturn(tDTO);
        Optional<TimeSheetDTO> result = timeSheetService.get(t.getId());
        assertEquals(Optional.of(tDTO), result);
        verify(timeSheetRepository).findById(t.getId());
        verify(employeeRepository).findById(e.getId());
        verify(genericMapper).convertToDto(Optional.of(t), TimeSheetDTO.class);
    }

    @Test
    void put(){
        when(timeSheetRepository.findById(t.getId())).thenReturn(Optional.of(newt));
        when(timeSheetRepository.existsByDateAndEmployeeIdAndIdNot(any(LocalDate.class), any(EmployeeModel.class), anyLong())).thenReturn(false);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(e));
        when(timeSheetRepository.save(any(TimeSheetModel.class))).thenReturn(newt);
        when(genericMapper.convertToDto(newt, TimeSheetDTO.class)).thenReturn(tDTO);
        TimeSheetDTO result = timeSheetService.put(t.getId(), newt);
        assertEquals(tDTO, result);
        verify(timeSheetRepository).findById(t.getId());
        verify(timeSheetRepository).existsByDateAndEmployeeIdAndIdNot(newt.getDate(), newt.getEmployeeId(), t.getId());
        verify(employeeRepository).findById(e.getId());
        verify(timeSheetRepository).save(newt);
        verify(genericMapper).convertToDto(newt, TimeSheetDTO.class);
    }

    @Test
    void delete(){
        when(timeSheetRepository.findById(t.getId())).thenReturn(Optional.of(t));
        timeSheetService.delete(t.getId());
        verify(timeSheetRepository, times(1)).deleteById(t.getId());
    }

    @Test
    void searchByYearAndMonth(){
        when(employeeRepository.findById(e.getId())).thenReturn(Optional.of(e));
        when(timeSheetRepository.findAllByYearAndMonth(
                t.getDate().getYear(),
                (byte) t.getDate().getMonth().getValue(),
                e.getId())
        ).thenReturn(Collections.singletonList(t));
        when(genericMapper.convertToDto(
                any(TimeSheetModel.class),
                eq(TimeSheetDTO.class)
        )).thenReturn(tDTO);
        List<TimeSheetDTO> result = timeSheetService.searchByYearAndMonth(
                e.getId(),
                t.getDate().getYear(),
                (byte) t.getDate().getMonth().getValue()
        );
        assertEquals(List.of(tDTO), result);
        verify(employeeRepository).findById(e.getId());
        verify(timeSheetRepository).findAllByYearAndMonth(
                t.getDate().getYear(),
                (byte) t.getDate().getMonth().getValue(),
                e.getId());
        verify(genericMapper).convertToDto(t, TimeSheetDTO.class);
    }
}