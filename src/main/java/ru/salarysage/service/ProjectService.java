package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.dto.ExpenditureDTO;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.dto.ProjectDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.ExpenditureException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.ProjectException;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.models.ProjectModel;
import ru.salarysage.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    protected final ProjectRepository projectRepository;
    protected final EmployeeService employeeService;
    protected final ExpenditureService expenditureService;
    public ProjectService(ProjectRepository projectRepository,
                          EmployeeService employeeService,
                          ExpenditureService expenditureService) {
        this.projectRepository = projectRepository;
        this.employeeService = employeeService;
        this.expenditureService = expenditureService;
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
        if(exDb.isEmpty()){
            throw new ExpenditureException.NoExpenditure();
        }
        if(p.getStartDate() == null){
            p.setStartDate(LocalDate.now());
        }
        projectRepository.save(p);

        Set<EmployeeDTO> eDTO = emDb.stream()
                .map(employeeModel -> new EmployeeDTO(
                        employeeModel.getLastName(),
                        employeeModel.getFirstName(),
                        employeeModel.getAddress(),
                        new PositionDTO(
                                employeeModel.getPosition().getName(),
                                employeeModel.getPosition().getRate()
                        )
                ))
                .collect(Collectors.toSet());

        Set<ExpenditureDTO> xDTO = exDb.stream()
                .map(expenditureModel -> new ExpenditureDTO(
                        expenditureModel.getName(),
                        expenditureModel.getAmount()
                ))
                .collect(Collectors.toSet());

        ProjectDTO pDTO = new ProjectDTO(
                p.getName(),
                p.getStartDate(),
                p.getEndDate(),
                eDTO,
                xDTO
        );

        return pDTO;
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
        Set<EmployeeDTO> eDTO = emDb.stream()
                .map(employeeModel -> new EmployeeDTO(
                        employeeModel.getLastName(),
                        employeeModel.getFirstName(),
                        employeeModel.getAddress(),
                        new PositionDTO(
                                employeeModel.getPosition().getName(),
                                employeeModel.getPosition().getRate()
                        )
                ))
                .collect(Collectors.toSet());

        Set<ExpenditureDTO> xDTO = exDb.stream()
                .map(expenditureModel -> new ExpenditureDTO(
                        expenditureModel.getName(),
                        expenditureModel.getAmount()
                ))
                .collect(Collectors.toSet());

        ProjectDTO pDTO = new ProjectDTO(
                p.get().getName(),
                p.get().getStartDate(),
                p.get().getEndDate(),
                eDTO,
                xDTO
        );
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
        if(exDb.isEmpty()){
            throw new ExpenditureException.NoExpenditure();
        }
        p.setId(id);
        projectRepository.save(p);
        Set<EmployeeDTO> eDTO = emDb.stream()
                .map(employeeModel -> new EmployeeDTO(
                        employeeModel.getLastName(),
                        employeeModel.getFirstName(),
                        employeeModel.getAddress(),
                        new PositionDTO(
                                employeeModel.getPosition().getName(),
                                employeeModel.getPosition().getRate()
                        )
                ))
                .collect(Collectors.toSet());

        Set<ExpenditureDTO> xDTO = exDb.stream()
                .map(expenditureModel -> new ExpenditureDTO(
                        expenditureModel.getName(),
                        expenditureModel.getAmount()
                ))
                .collect(Collectors.toSet());

        ProjectDTO pDTO = new ProjectDTO(
                p.getName(),
                p.getStartDate(),
                p.getEndDate(),
                eDTO,
                xDTO
        );
        return pDTO;
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
