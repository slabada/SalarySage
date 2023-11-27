package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.exception.BenefitException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.models.BenefitModel;
import ru.salarysage.models.PaySheetModel;
import ru.salarysage.repository.BenefitRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BenefitService {
    protected final BenefitRepository benefitRepository;
    public BenefitService(BenefitRepository benefitRepository) {
        this.benefitRepository = benefitRepository;
    }

    public BenefitModel create(BenefitModel b){
        boolean nDb = benefitRepository.existsByName(b.getName());
        if(nDb){
            throw new BenefitException.BenefitAlreadyExistsException();
        }
        benefitRepository.save(b);
        return b;
    }
    public Optional<BenefitModel> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<BenefitModel> bDb = benefitRepository.findById(id);
        if(bDb.isEmpty()){
            throw new BenefitException.NullBenefitException();
        }
        return bDb;
    }
    public BenefitModel put (long id, BenefitModel b){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<BenefitModel> bDb = benefitRepository.findById(id);
        if(bDb.isEmpty()){
            throw new BenefitException.NullBenefitException();
        }
        boolean nDb = benefitRepository.existsByNameAndIdNot(b.getName(), id);
        if(nDb){
            throw new BenefitException.BenefitAlreadyExistsException();
        }
        b.setId(id);
        benefitRepository.save(b);
        return b;
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<BenefitModel> bDb = benefitRepository.findById(id);
        if(bDb.isEmpty()){
            throw new BenefitException.NullBenefitException();
        }
        benefitRepository.deleteById(id);
    }
    // Метод для проверки льгот, связанных с расчетным листком.
    public Set<BenefitModel> check(PaySheetModel pc) {
        Set<BenefitModel> result = new HashSet<>();
        if (pc.getBenefit() != null) {
            List<Long> b = pc.getBenefit().stream()
                    .map(BenefitModel::getId)
                    .toList();
            List<BenefitModel> bDb = benefitRepository.findAllById(b);
            result.addAll(bDb);
        }
        return result;
    }
}
