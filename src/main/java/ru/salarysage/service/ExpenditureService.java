package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.exception.ExpenditureException;
import ru.salarysage.exception.GeneraleException;
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

    public ExpenditureModel create(ExpenditureModel e){
        boolean eDb = expenditureRepository.existsByName(e.getName());
        if(eDb){
            throw new ExpenditureException.ConflictName();
        }
        expenditureRepository.save(e);
        return e;
    }
    public Optional<ExpenditureModel> get(long id){
        if(id <= 0) {
            throw new GeneraleException.InvalidIdException();
        }
        Optional<ExpenditureModel> eDb = expenditureRepository.findById(id);
        if(eDb.isEmpty()){
            throw new ExpenditureException.NoExpenditure();
        }
        return eDb;
    }
    public ExpenditureModel put(long id, ExpenditureModel e){
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
        return e;
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
