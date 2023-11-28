package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.dto.ExpenditureDTO;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.exception.ExpenditureException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.models.ProjectModel;
import ru.salarysage.repository.ExpenditureRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ExpenditureService {
    protected final ExpenditureRepository expenditureRepository;
    public ExpenditureService(ExpenditureRepository expenditureRepository) {
        this.expenditureRepository = expenditureRepository;
    }

    public ExpenditureDTO create(ExpenditureModel e){
        boolean eDb = expenditureRepository.existsByName(e.getName());
        if(eDb){
            throw new ExpenditureException.ConflictName();
        }
        expenditureRepository.save(e);
        ExpenditureDTO eDTO = new ExpenditureDTO(
                e.getName(),
                e.getAmount()
        );
        return eDTO;
    }
    public Optional<ExpenditureDTO> get(long id){
        if(id <= 0) {
            throw new GeneraleException.InvalidIdException();
        }
        Optional<ExpenditureModel> eDb = expenditureRepository.findById(id);
        if(eDb.isEmpty()){
            throw new ExpenditureException.NoExpenditure();
        }
        ExpenditureDTO eDTO = new ExpenditureDTO(
                eDb.get().getName(),
                eDb.get().getAmount()
        );
        return Optional.of(eDTO);
    }
    public ExpenditureDTO put(long id, ExpenditureModel e){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<ExpenditureModel> eDb = expenditureRepository.findById(id);
        if(eDb.isEmpty()){
            throw new ExpenditureException.NoExpenditure();
        }
        boolean nDb = expenditureRepository.existsByNameAndIdNot(e.getName(), id);
        if(nDb){
            throw new ExpenditureException.ConflictName();
        }
        e.setId(id);
        expenditureRepository.save(e);
        ExpenditureDTO eDTO = new ExpenditureDTO(
                e.getName(),
                e.getAmount()
        );
        return eDTO;
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<ExpenditureModel> eDb = expenditureRepository.findById(id);
        if(eDb.isEmpty()){
            throw new ExpenditureException.NoExpenditure();
        }
        expenditureRepository.deleteById(id);
    }
    // Метод для проверки доп затрат, связанных с проектом.
    public Set<ExpenditureModel> check(ProjectModel p) {
        Set<ExpenditureModel> result = new HashSet<>();
        if (p.getExpenditure() != null) {
            List<Long> b = p.getExpenditure().stream()
                    .map(ExpenditureModel::getId)
                    .toList();
            List<ExpenditureModel> bDb = expenditureRepository.findAllById(b);

            result.addAll(bDb);
        }
        return result;
    }
}
