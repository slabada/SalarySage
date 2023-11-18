package ru.salarysage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.ExpenditureException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.ProjectException;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.models.PositionModel;
import ru.salarysage.models.ProjectModel;
import ru.salarysage.repository.ProjectRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

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

    private ProjectModel pr;

    private ProjectModel newpr;

    private ExpenditureModel ex;

    private EmployeeModel e;

    private PositionModel p;

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

        ex = new ExpenditureModel();

        ex.setId(1L);
        ex.setName("Test");
        ex.setAmount(BigDecimal.valueOf(6666));

        pr = new ProjectModel();

        pr.setId(1L);
        pr.setName("Test");
        pr.setStartDate(LocalDate.now());
        pr.setEndDate(LocalDate.now().plusDays(15));
        pr.setEmployees(Collections.singleton(e));
        pr.setExpenditure(Collections.singleton(ex));

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

        when(projectRepository.existsByName(p.getName())).thenReturn(false);

        when(employeeService.check(pr)).thenReturn(Collections.singleton(e));

        when(expenditureService.check(pr)).thenReturn(Collections.singleton(ex));

        ProjectModel r = projectService.create(pr);

        verify(projectRepository, times(1)).save(pr);

        assertEquals(r, pr);
    }

    @Test
    void get(){

        when(projectRepository.findById(pr.getId())).thenReturn(Optional.ofNullable(pr));

        Optional<ProjectModel> r = projectService.get(pr.getId());

        assertTrue(r.isPresent());
        assertEquals(pr,r.get());
    }

    @Test
    void put(){

        when(projectRepository.findById(pr.getId())).thenReturn(Optional.ofNullable(pr));

        when(projectRepository.existsByNameAndIdNot(newpr.getName(), pr.getId())).thenReturn(false);

        when(employeeService.check(newpr)).thenReturn(Collections.singleton(e));

        when(expenditureService.check(newpr)).thenReturn(Collections.singleton(ex));

        ProjectModel r = projectService.put(pr.getId(), newpr);

        verify(projectRepository, times(1)).save(newpr);

        assertEquals(newpr , r);
    }

    @Test
    void delete(){

        when(projectRepository.findById(pr.getId())).thenReturn(Optional.ofNullable(pr));

        projectService.delete(p.getId());

        verify(projectRepository, times(1)).deleteById(pr.getId());
    }
}