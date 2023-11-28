package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.PositionModel;
import ru.salarysage.repository.EmployeeRepository;
import ru.salarysage.repository.PositionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @InjectMocks
    private EmployeeService employeeService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private GenericMapper genericMapper;
    private PositionModel p;
    private PositionDTO pDTO;
    private EmployeeModel e;
    private EmployeeDTO eDTO;
    private EmployeeModel newe;
    @BeforeEach
    void setUp() {
        p = new PositionModel();
        p.setId(1L);
        p.setName("Test");
        p.setRate(new BigDecimal(50000));

        pDTO = new PositionDTO();
        pDTO.setName("Test");
        pDTO.setRate(BigDecimal.valueOf(50000));

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

        newe = new EmployeeModel();
        newe.setId(1L);
        newe.setLastName("newTest");
        newe.setFirstName("newTest");
        newe.setAddress("newTest");
        newe.setPosition(p);
    }

    @Test
    public void positionNotFoundException() {
        when(positionRepository.findById(e.getPosition().getId())).thenReturn(Optional.empty());
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
        when(positionRepository.findById(anyLong())).thenReturn(Optional.of(p));
        when(employeeRepository.save(any(EmployeeModel.class))).thenReturn(e);
        when(genericMapper.convertToDto(any(EmployeeModel.class), eq(EmployeeDTO.class))).thenReturn(eDTO);
        EmployeeDTO result = employeeService.create(e);
        assertEquals(eDTO, result);
        verify(positionRepository).findById(p.getId());
        verify(employeeRepository).save(e);
        verify(genericMapper).convertToDto(e, EmployeeDTO.class);
    }

    @Test
    void get(){
        when(employeeRepository.findById(e.getId())).thenReturn(Optional.of(e));
        when(positionRepository.findById(anyLong())).thenReturn(Optional.of(p));
        when(genericMapper.convertToDto(Optional.of(e), EmployeeDTO.class)).thenReturn(eDTO);
        Optional<EmployeeDTO> result = employeeService.get(e.getId());
        assertEquals(Optional.of(eDTO), result);
        verify(employeeRepository).findById(e.getId());
        verify(positionRepository).findById(p.getId());
        verify(genericMapper).convertToDto(Optional.of(e), EmployeeDTO.class);
    }

    @Test
    void put(){
        when(employeeRepository.findById(e.getId())).thenReturn(Optional.of(e));
        when(positionRepository.findById(anyLong())).thenReturn(Optional.of(p));
        when(employeeRepository.save(any(EmployeeModel.class))).thenReturn(newe);
        when(genericMapper.convertToDto(newe, EmployeeDTO.class)).thenReturn(eDTO);
        EmployeeDTO result = employeeService.put(e.getId(), newe);
        assertEquals(eDTO, result);
        verify(employeeRepository).findById(e.getId());
        verify(positionRepository).findById(p.getId());
        verify(employeeRepository).save(newe);
        verify(genericMapper).convertToDto(newe, EmployeeDTO.class);
    }

    @Test
    void delete(){
        when(employeeRepository.findById(e.getId())).thenReturn(Optional.of(e));
        employeeService.delete(e.getId());
        verify(employeeRepository, times(1)).deleteById(e.getId());
    }

    @Test
    void search(){
        when(employeeRepository.search(e, PageRequest.of(1, 1))).thenReturn(List.of(eDTO));
        List<EmployeeDTO> re = employeeService.search(e, 1, 1);
        assertThat(re).usingRecursiveComparison().isEqualTo(List.of(e));
    }
}