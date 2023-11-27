package ru.salarysage.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.models.*;
import ru.salarysage.repository.EmployeeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EmployeeService {
    protected final EmployeeRepository employeeRepository;
    protected final PositionService positionService;
    public EmployeeService(EmployeeRepository employeeRepository,
                           PositionService positionService) {
        this.employeeRepository = employeeRepository;
        this.positionService = positionService;
    }

    public EmployeeModel create(EmployeeModel e){
        Optional<PositionModel> p = positionService.get(e.getPosition().getId());
        if(p.isEmpty()){
            throw new PositionException.PositionNotFoundException();
        }
        e.setPosition(p.get());
        employeeRepository.save(e);
        return e;
    }
    public Optional<EmployeeModel> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<EmployeeModel> e = employeeRepository.findById(id);
        if(e.isEmpty()) {
            throw new EmployeeException.EmployeeNotFoundException();
        }
        return e;
    }
    public EmployeeModel put(long id, EmployeeModel e){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<EmployeeModel> eDb = employeeRepository.findById(id);
        if(eDb.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        Optional<PositionModel> pDb = positionService.get(e.getPosition().getId());
        if(pDb.isEmpty()){
            throw new PositionException.PositionNotFoundException();
        }
        e.setId(id);
        e.setPosition(pDb.get());
        employeeRepository.save(e);
        return e;
    }
    public void delete(long id){
        if(id <= 0) {
            throw new GeneraleException.InvalidIdException();
        }
        Optional<EmployeeModel> e = employeeRepository.findById(id);
        if(e.isEmpty()) {
            throw new EmployeeException.EmployeeNotFoundException();
        }
        employeeRepository.deleteById(id);
    }
    public List<EmployeeModel> search(EmployeeModel e, int from, int size){
        if(from < 0 || size <= 0) {
            throw new EmployeeException.InvalidPageSizeException();
        }
        PageRequest page = PageRequest.of(from, size);
        return employeeRepository.search(e, page);
    }
    // Метод для проверки сотрудников, связанных с проектом.
    public Set<EmployeeModel> check(ProjectModel p) {
        Set<EmployeeModel> result = new HashSet<>();
        if (p.getEmployees() != null) {
            List<Long> b = p.getEmployees().stream()
                    .map(EmployeeModel::getId)
                    .toList();
            List<EmployeeModel> bDb = employeeRepository.findAllById(b);
            result.addAll(bDb);
        }
        return result;
    }
}
