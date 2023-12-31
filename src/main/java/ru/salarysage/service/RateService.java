package ru.salarysage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.salarysage.dto.RateDTO;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.RateException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.PaySheetModel;
import ru.salarysage.models.RateModel;
import ru.salarysage.repository.RateRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RateService {

    protected final RateRepository rateRepository;
    protected final GenericMapper genericMapper;

    public RateDTO create(RateModel r){
        boolean nBd = rateRepository.existsByName(r.getName());
        if(nBd){
            throw new RateException.RateAlreadyExistsException();
        }
        RateModel save = rateRepository.save(r);
        return genericMapper.convertToDto(save, RateDTO.class);
    }
    public Optional<RateDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        RateModel r = rateRepository.findById(id)
                .orElseThrow(RateException.NullRateException::new);
        RateDTO rDTO = genericMapper.convertToDto(r, RateDTO.class);
        return Optional.of(rDTO);
    }
    public RateDTO put(long id, RateModel r){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        rateRepository.findById(id)
                .orElseThrow(RateException.NullRateException::new);
        boolean nBd = rateRepository.existsByNameAndIdNot(r.getName(), id);
        if(nBd){
            throw new RateException.RateAlreadyExistsException();
        }
        r.setId(id);
        RateModel save = rateRepository.save(r);
        return genericMapper.convertToDto(save, RateDTO.class);
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        rateRepository.findById(id)
                .orElseThrow(RateException.NullRateException::new);
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
