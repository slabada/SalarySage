package ru.salarysage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.salarysage.dto.ExpenditureDTO;
import ru.salarysage.exception.ExpenditureException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.models.ProjectModel;
import ru.salarysage.repository.ExpenditureRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenditureService {

    protected final ExpenditureRepository expenditureRepository;
    protected final GenericMapper genericMapper;

    public ExpenditureDTO create(ExpenditureModel e){
        boolean eDb = expenditureRepository.existsByName(e.getName());
        if(eDb){
            throw new ExpenditureException.ConflictName();
        }
        ExpenditureModel save = expenditureRepository.save(e);
        return genericMapper.convertToDto(save, ExpenditureDTO.class);
    }
    public Optional<ExpenditureDTO> get(long id){
        if(id <= 0) {
            throw new GeneraleException.InvalidIdException();
        }
        ExpenditureModel eDb = expenditureRepository.findById(id)
                .orElseThrow(ExpenditureException.NoExpenditure::new);
        ExpenditureDTO eDTO = genericMapper.convertToDto(eDb, ExpenditureDTO.class);
        return Optional.of(eDTO);
    }
    public ExpenditureDTO put(long id, ExpenditureModel e){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        expenditureRepository.findById(id)
                .orElseThrow(ExpenditureException.NoExpenditure::new);
        boolean nDb = expenditureRepository.existsByNameAndIdNot(e.getName(), id);
        if(nDb){
            throw new ExpenditureException.ConflictName();
        }
        e.setId(id);
        ExpenditureModel save = expenditureRepository.save(e);
        return genericMapper.convertToDto(save, ExpenditureDTO.class);
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        expenditureRepository.findById(id)
                .orElseThrow(ExpenditureException.NoExpenditure::new);
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
