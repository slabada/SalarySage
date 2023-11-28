package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.dto.ExpenditureDTO;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.dto.ProjectDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.ExpenditureException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.ProjectException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.models.PositionModel;
import ru.salarysage.models.ProjectModel;
import ru.salarysage.repository.ProjectRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private ExpenditureService expenditureService;
    @Mock
    private GenericMapper genericMapper;
    private ProjectModel pr;
    private ProjectDTO prDTO;
    private ProjectModel newpr;
    private ExpenditureModel ex;
    private ExpenditureDTO exDTO;
    private EmployeeModel e;
    private EmployeeDTO eDTO;
    private PositionModel p;
    private PositionDTO pDTO;
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

        ex = new ExpenditureModel();
        ex.setId(1L);
        ex.setName("Test");
        ex.setAmount(BigDecimal.valueOf(6666));

        exDTO = new ExpenditureDTO();
        exDTO.setName("Test");
        exDTO.setAmount(BigDecimal.valueOf(6666));

        pr = new ProjectModel();
        pr.setId(1L);
        pr.setName("Test");
        pr.setStartDate(LocalDate.now());
        pr.setEndDate(LocalDate.now().plusDays(15));
        pr.setEmployees(Collections.singleton(e));
        pr.setExpenditure(Collections.singleton(ex));

        prDTO = new ProjectDTO();
        prDTO.setName("Test");
        prDTO.setStartDate(LocalDate.now());
        prDTO.setEndDate(LocalDate.now().plusDays(15));
        prDTO.setEmployees(Collections.singleton(eDTO));
        prDTO.setExpenditure(Collections.singleton(exDTO));

        newpr = new ProjectModel();
        newpr.setId(1L);
        newpr.setName("newTest");
        newpr.setStartDate(LocalDate.now());
        newpr.setEndDate(LocalDate.now().plusDays(20));
        newpr.setEmployees(Collections.singleton(e));
        newpr.setExpenditure(Collections.singleton(ex));
    }

    @Test
    void projectConflictName(){
        when(projectRepository.existsByName(pr.getName())).thenReturn(true);
        assertThrows(ProjectException.ConflictName.class, () -> {
            projectService.create(pr);
            projectService.put(pr.getId(), newpr);
        });
    }

    @Test
    public void employeeNotFoundException() {
        assertThrows(EmployeeException.EmployeeNotFoundException.class, () -> {
            projectService.create(pr);
            projectService.put(pr.getId(), newpr);
        });
    }

    @Test
    public void invalidIdException() {
        assertThrows(GeneraleException.InvalidIdException.class, () -> {
            projectService.get(-1L);
            projectService.put(-1L, newpr);
            projectService.delete(-1L);
        });
    }

    @Test
    void noExpenditure(){
        when(employeeService.check(pr)).thenReturn(Collections.singleton(e));
        assertThrows(ExpenditureException.NoExpenditure.class, () -> {
            projectService.create(pr);
            projectService.put(pr.getId(), newpr);
        });
    }

    @Test
    void noProject(){
        when(projectRepository.findById(e.getId())).thenReturn(Optional.empty());
        assertThrows(ProjectException.NoProject.class, () -> {
            projectService.get(pr.getId());
            projectService.put(pr.getId(), newpr);
            projectService.delete(pr.getId());
        });
    }

    @Test
    void create() {
        when(projectRepository.existsByName(anyString())).thenReturn(false);
        when(employeeService.check(any(ProjectModel.class))).thenReturn(Collections.singleton(e));
        when(expenditureService.check(any(ProjectModel.class))).thenReturn(Collections.singleton(ex));
        when(projectRepository.save(any(ProjectModel.class))).thenReturn(pr);
        when(genericMapper.convertToDto(any(ProjectModel.class), eq(ProjectDTO.class))).thenReturn(prDTO);
        ProjectDTO result = projectService.create(pr);
        assertEquals(prDTO, result);
        verify(projectRepository).existsByName(p.getName());
        verify(employeeService).check(pr);
        verify(expenditureService).check(pr);
        verify(projectRepository).save(pr);
        verify(genericMapper).convertToDto(pr, ProjectDTO.class);
    }

    @Test
    void get(){
        when(projectRepository.findById(pr.getId())).thenReturn(Optional.of(pr));
        when(employeeService.check(any(ProjectModel.class))).thenReturn(Collections.singleton(e));
        when(expenditureService.check(any(ProjectModel.class))).thenReturn(Collections.singleton(ex));
        when(genericMapper.convertToDto(Optional.of(pr), ProjectDTO.class)).thenReturn(prDTO);
        Optional<ProjectDTO> result = projectService.get(pr.getId());
        assertEquals(Optional.of(prDTO), result);
        verify(projectRepository).findById(pr.getId());
        verify(employeeService).check(pr);
        verify(expenditureService).check(pr);
        verify(genericMapper).convertToDto(Optional.of(pr), ProjectDTO.class);
    }

    @Test
    void put(){
        when(projectRepository.findById(pr.getId())).thenReturn(Optional.of(pr));
        when(projectRepository.existsByNameAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(employeeService.check(any(ProjectModel.class))).thenReturn(Collections.singleton(e));
        when(expenditureService.check(any(ProjectModel.class))).thenReturn(Collections.singleton(ex));
        when(projectRepository.save(any(ProjectModel.class))).thenReturn(newpr);
        when(genericMapper.convertToDto(newpr, ProjectDTO.class)).thenReturn(prDTO);
        ProjectDTO result = projectService.put(pr.getId(), newpr);
        assertEquals(prDTO, result);
        verify(projectRepository).findById(pr.getId());
        verify(projectRepository).existsByNameAndIdNot(newpr.getName(), pr.getId());
        verify(employeeService).check(newpr);
        verify(expenditureService).check(newpr);
        verify(projectRepository).save(newpr);
        verify(genericMapper).convertToDto(newpr, ProjectDTO.class);
    }

    @Test
    void delete(){
        when(projectRepository.findById(pr.getId())).thenReturn(Optional.ofNullable(pr));
        projectService.delete(p.getId());
        verify(projectRepository, times(1)).deleteById(pr.getId());
    }
}