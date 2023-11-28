package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.BenefitDTO;
import ru.salarysage.dto.TimeSheetDTO;
import ru.salarysage.exception.BenefitException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.mapper.GenericMapper;
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
    protected final GenericMapper genericMapper;
    public BenefitService(BenefitRepository benefitRepository,
                          GenericMapper genericMapper) {
        this.benefitRepository = benefitRepository;
        this.genericMapper = genericMapper;
    }

    public BenefitDTO create(BenefitModel b){
        boolean nDb = benefitRepository.existsByName(b.getName());
        if(nDb){
            throw new BenefitException.BenefitAlreadyExistsException();
        }
        BenefitModel save = benefitRepository.save(b);
        BenefitDTO bDTO = genericMapper.convertToDto(save, BenefitDTO.class);
        return bDTO;
    }
    public Optional<BenefitDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<BenefitModel> bDb = benefitRepository.findById(id);
        if(bDb.isEmpty()){
            throw new BenefitException.NullBenefitException();
        }
        BenefitDTO bDTO = genericMapper.convertToDto(bDb, BenefitDTO.class);
        return Optional.of(bDTO);
    }
    public BenefitDTO put (long id, BenefitModel b){
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
        BenefitModel save = benefitRepository.save(b);
        BenefitDTO bDTO = genericMapper.convertToDto(save, BenefitDTO.class);
        return bDTO;
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
