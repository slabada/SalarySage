package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.RateDTO;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.RateException;
import ru.salarysage.models.PaySheetModel;
import ru.salarysage.models.RateModel;
import ru.salarysage.repository.RateRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RateService {
    protected final RateRepository rateRepository;
    public RateService(RateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public RateDTO create(RateModel r){
        boolean nBd = rateRepository.existsByName(r.getName());
        if(nBd){
            throw new RateException.RateAlreadyExistsException();
        }
        rateRepository.save(r);
        RateDTO rDTO = new RateDTO(
                r.getName(),
                r.getPercent()
        );
        return rDTO;
    }
    public Optional<RateDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<RateModel> r = rateRepository.findById(id);
        if(r.isEmpty()){
            throw new RateException.NullRateException();
        }
        RateDTO rDTO = new RateDTO(
                r.get().getName(),
                r.get().getPercent()
        );
        return Optional.of(rDTO);
    }
    public RateDTO put(long id, RateModel r){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<RateModel> rDb = rateRepository.findById(id);
        if(rDb.isEmpty()){
            throw new RateException.NullRateException();
        }
        boolean nBd = rateRepository.existsByNameAndIdNot(r.getName(), id);
        if(nBd){
            throw new RateException.RateAlreadyExistsException();
        }
        r.setId(id);
        rateRepository.save(r);
        RateDTO rDTO = new RateDTO(
                r.getName(),
                r.getPercent()
        );
        return rDTO;
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<RateModel> rDb = rateRepository.findById(id);
        if(rDb.isEmpty()){
            throw new RateException.NullRateException();
        }
        rateRepository.deleteById(id);
    }
    // Метод для проверки налога, связанных с расчетным листком.
    public Set<RateModel> check(PaySheetModel prc) {
        Set<RateModel> result = new HashSet<>();
        if (prc.getRate() != null && prc.getRate().size() > 0) {
            List<Long> r = prc.getRate().stream()
                    .map(RateModel::getId)
                    .toList();
            List<RateModel> rDb = rateRepository.findAllById(r);
            result.addAll(rDb);
            boolean hasNDFL = rDb.stream().map(RateModel::getName).anyMatch("НДФЛ"::equals);
            if(!hasNDFL){
                // Если нет ставки "НДФЛ", добавляем её в результат.
                Optional<RateModel> NDFL = rateRepository.findByName("НДФЛ");
                NDFL.ifPresent(result::add);
            }
        }
        else {
            // Если нет указанных ставок, добавляем ставку "НДФЛ" в результат.
            Optional<RateModel> NDFL = rateRepository.findByName("НДФЛ");
            NDFL.ifPresent(result::add);
        }
        return result;
    }
}
