package ru.salarysage.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.models.*;
import ru.salarysage.repository.EmployeeRepository;

import java.util.*;

@Service
public class EmployeeService {
    protected final EmployeeRepository employeeRepository;
    protected final PositionService positionService;
    public EmployeeService(EmployeeRepository employeeRepository,
                           PositionService positionService) {
        this.employeeRepository = employeeRepository;
        this.positionService = positionService;
    }

    public EmployeeDTO create(EmployeeModel e){
        Optional<PositionDTO> p = positionService.get(e.getPosition().getId());
        if(p.isEmpty()){
            throw new PositionException.PositionNotFoundException();
        }
        e.setPosition(
                new PositionModel(
                        e.getPosition().getId(),
                        p.get().getName(),
                        p.get().getRate()
                )
        );
        employeeRepository.save(e);
        EmployeeDTO eDTO = new EmployeeDTO(
                e.getFirstName(),
                e.getLastName(),
                e.getAddress(),
                p.get()
        );
        return eDTO;
    }
    public Optional<EmployeeDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<EmployeeModel> e = employeeRepository.findById(id);
        if(e.isEmpty()) {
            throw new EmployeeException.EmployeeNotFoundException();
        }
        Optional<PositionDTO> p = positionService.get(e.get().getPosition().getId());
        if(p.isEmpty()){
            throw new PositionException.PositionNotFoundException();
        }
        EmployeeDTO eDTO = new EmployeeDTO(
                e.get().getFirstName(),
                e.get().getLastName(),
                e.get().getAddress(),
                p.get()
        );
        return Optional.of(eDTO);
    }
    public EmployeeDTO put(long id, EmployeeModel e){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<EmployeeModel> eDb = employeeRepository.findById(id);
        if(eDb.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        Optional<PositionDTO> pDb = positionService.get(e.getPosition().getId());
        if(pDb.isEmpty()){
            throw new PositionException.PositionNotFoundException();
        }
        e.setId(id);
        e.setPosition(
                new PositionModel(
                        e.getPosition().getId(),
                        pDb.get().getName(),
                        pDb.get().getRate()
                )
        );
        employeeRepository.save(e);
        EmployeeDTO eDTO = new EmployeeDTO(
                e.getFirstName(),
                e.getLastName(),
                e.getAddress(),
                pDb.get()
        );
        return eDTO;
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
    public List<EmployeeDTO> search(EmployeeModel e, int from, int size){
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
            List<Long> employeeIds = p.getEmployees().stream()
                    .map(EmployeeModel::getId)
                    .toList();

            List<EmployeeModel> eDb = employeeRepository.findAllById(employeeIds);

//            // Перевод EmployeeModel в EmployeeDTO
//            for (EmployeeModel employeeModel : eDb) {
//                Optional<PositionDTO> pDTO = positionService.get(employeeModel.getPosition().getId());
//                if(pDTO.isEmpty()){
//                    throw new PositionException.PositionNotFoundException();
//                }
//                EmployeeDTO employeeDTO = new EmployeeDTO(
//                        employeeModel.getFirstName(),
//                        employeeModel.getLastName(),
//                        employeeModel.getAddress(),
//                        pDTO.get()
//                );
//                result.add(employeeDTO);
//            }

            result.addAll(eDb);
        }
        return result;
    }
}
