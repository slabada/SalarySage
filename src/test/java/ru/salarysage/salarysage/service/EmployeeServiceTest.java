package ru.salarysage.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.salarysage.salarysage.exception.EmployeeException;
import ru.salarysage.salarysage.exception.GeneraleException;
import ru.salarysage.salarysage.exception.PositionException;
import ru.salarysage.salarysage.models.EmployeeModel;
import ru.salarysage.salarysage.models.PositionModel;
import ru.salarysage.salarysage.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @InjectMocks
    private EmployeeService employeeService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PositionService positionService;

    private PositionModel p;

    private EmployeeModel e;

    private EmployeeModel newe;

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

        newe = new EmployeeModel();

        newe.setId(1L);
        newe.setLastName("newTest");
        newe.setFirstName("newTest");
        newe.setAddress("newTest");
        newe.setPosition(p);

    }

    @Test
    public void positionNotFoundException() {

        when(positionService.get(e.getPosition().getId())).thenReturn(Optional.empty());

        assertThrows(PositionException.PositionNotFoundException.class, () -> {
            employeeService.create(e);
            employeeService.put(p.getId(), e);
        });
    }

    @Test
    public void employeeNotFoundException() {

        when(employeeRepository.findById(e.getId())).thenReturn(Optional.empty());

        assertThrows(EmployeeException.EmployeeNotFoundException.class, () -> {
            employeeService.get(e.getId());
            employeeService.put(e.getId(), e);
            employeeService.delete(e.getId());
        });
    }

    @Test
    public void invalidIdException() {

        assertThrows(GeneraleException.InvalidIdException.class, () -> {
            employeeService.get(-1L);
            employeeService.put(-1L, e);
            employeeService.delete(-1L);
        });
    }

    @Test
    public void invalidPageSizeException() {

        assertThrows(EmployeeException.InvalidPageSizeException.class, () -> {
            employeeService.search(e, -1, -1);
        });
    }

    @Test
    void create() {

        when(positionService.get(e.getPosition().getId())).thenReturn(Optional.of(p));

        employeeService.create(e);

        verify(employeeRepository, times(1)).save(e);
    }

    @Test
    void get(){

        when(employeeRepository.findById(e.getId())).thenReturn(Optional.of(e));

        Optional<EmployeeModel> re = employeeService.get(e.getId());

        assertTrue(re.isPresent());
        assertEquals(e, re.get());
    }

    @Test
    void put(){

        when(employeeRepository.findById(e.getId())).thenReturn(Optional.of(e));

        when(positionService.get(e.getPosition().getId())).thenReturn(Optional.of(p));

        EmployeeModel re = employeeService.put(e.getId(), newe);

        assertEquals(newe, re);

        verify(employeeRepository, times(1)).save(newe);
    }

    @Test
    void delete(){

        when(employeeRepository.findById(e.getId())).thenReturn(Optional.of(e));

        employeeService.delete(e.getId());

        verify(employeeRepository, times(1)).deleteById(e.getId());
    }

    @Test
    void search(){

        when(employeeRepository.search(e, PageRequest.of(1, 1))).thenReturn(List.of(e));

        List<EmployeeModel> re = employeeService.search(e, 1, 1);

        assertEquals(List.of(e), re);
    }
}