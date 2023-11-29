package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.ProjectDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.ExpenditureException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.ProjectException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.models.ProjectModel;
import ru.salarysage.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
public class ProjectService {
    protected final ProjectRepository projectRepository;
    protected final EmployeeService employeeService;
    protected final ExpenditureService expenditureService;
    protected final GenericMapper genericMapper;
    public ProjectService(ProjectRepository projectRepository,
                          EmployeeService employeeService,
                          ExpenditureService expenditureService,
                          GenericMapper genericMapper) {
        this.projectRepository = projectRepository;
        this.employeeService = employeeService;
        this.expenditureService = expenditureService;
        this.genericMapper = genericMapper;
    }

    public ProjectDTO create(ProjectModel p){
        boolean pDb = projectRepository.existsByName(p.getName());
        if(pDb){
            throw new ProjectException.ConflictName();
        }
        Set<EmployeeModel> emDb = employeeService.check(p);
        p.setEmployees(emDb);
        if(emDb.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        Set<ExpenditureModel> exDb = expenditureService.check(p);
        p.setExpenditure(exDb);
        if(p.getStartDate() == null){
            p.setStartDate(LocalDate.now());
        }
        ProjectModel save = projectRepository.save(p);
        return genericMapper.convertToDto(save, ProjectDTO.class);
    }
    public Optional<ProjectDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<ProjectModel> p = projectRepository.findById(id);
        if(p.isEmpty()){
            throw new ProjectException.NoProject();
        }
        Set<EmployeeModel> emDb = employeeService.check(p.get());
        if(emDb.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        Set<ExpenditureModel> exDb = expenditureService.check(p.get());
        if(exDb.isEmpty()){
            throw new ExpenditureException.NoExpenditure();
        }
        ProjectDTO pDTO = genericMapper.convertToDto(p, ProjectDTO.class);
        return Optional.of(pDTO);
    }
    public ProjectDTO put(long id, ProjectModel p){
        if(id <= 0) {
            throw new GeneraleException.InvalidIdException();
        }
        Optional<ProjectModel> pDb = projectRepository.findById(id);
        if(pDb.isEmpty()) {
            throw new ProjectException.NoProject();
        }
        boolean nDb = projectRepository.existsByNameAndIdNot(p.getName(), id);
        if(nDb){
            throw new ProjectException.ConflictName();
        }
        Set<EmployeeModel> emDb = employeeService.check(p);
        p.setEmployees(emDb);
        if(emDb.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        Set<ExpenditureModel> exDb = expenditureService.check(p);
        p.setExpenditure(exDb);
        p.setId(id);
        ProjectModel save = projectRepository.save(p);
        return genericMapper.convertToDto(save, ProjectDTO.class);
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<ProjectModel> p = projectRepository.findById(id);
        if(p.isEmpty()){
            throw new ProjectException.NoProject();
        }
        projectRepository.deleteById(id);
    }
}
