package ru.salarysage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.PositionModel;
import ru.salarysage.models.ProjectModel;
import ru.salarysage.repository.EmployeeRepository;
import ru.salarysage.repository.PositionRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    protected final EmployeeRepository employeeRepository;
    protected final PositionRepository positionRepository;
    protected final GenericMapper genericMapper;

    public EmployeeDTO create(EmployeeModel e){
        PositionModel p = positionRepository.findById(e.getPosition().getId())
                .orElseThrow(PositionException.PositionNotFoundException::new);
        e.setPosition(p);
        EmployeeModel save = employeeRepository.save(e);
        return genericMapper.convertToDto(save, EmployeeDTO.class);
    }
    public Optional<EmployeeDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        EmployeeModel e = employeeRepository.findById(id)
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        EmployeeDTO eDTO = genericMapper.convertToDto(e, EmployeeDTO.class);
        return Optional.of(eDTO);
    }
    public EmployeeDTO put(long id, EmployeeModel e){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        employeeRepository.findById(id)
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        PositionModel pDb = positionRepository.findById(e.getPosition().getId())
                .orElseThrow(PositionException.PositionNotFoundException::new);
        e.setId(id);
        e.setPosition(pDb);
        EmployeeModel save = employeeRepository.save(e);
        return genericMapper.convertToDto(save, EmployeeDTO.class);
    }
    public void delete(long id){
        if(id <= 0) {
            throw new GeneraleException.InvalidIdException();
        }
        employeeRepository.findById(id)
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
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
            result.addAll(eDb);
        }
        return result;
    }
}
