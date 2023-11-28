package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.models.PositionModel;
import ru.salarysage.repository.PositionRepository;

import java.util.Optional;

@Service
public class PositionService {
    protected final PositionRepository positionRepository;
    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public PositionDTO create(PositionModel p) {
        boolean pByName = positionRepository.existsByName(p.getName());
        if (pByName) {
            throw new PositionException.PositionAlreadyExistsException();
        }
        positionRepository.save(p);
        PositionDTO pDTO = new PositionDTO(
                p.getName(),
                p.getRate()
        );
        return pDTO;
    }
    public Optional<PositionDTO> get(long id) {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<PositionModel> p = positionRepository.findById(id);
        if (p.isEmpty()){
            throw new PositionException.PositionNotFoundException();
        }
        PositionDTO pDTO = new PositionDTO(
                p.get().getName(),
                p.get().getRate()
        );
        return Optional.of(pDTO);
    }
    public PositionDTO put(long id, PositionModel p) {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<PositionModel> pDb = positionRepository.findById(id);
        if (pDb.isEmpty()){
            throw new PositionException.PositionNotFoundException();
        }
        boolean nDb = positionRepository.existsByNameAndIdNot(p.getName(), id);
        if(nDb){
            throw new PositionException.PositionAlreadyExistsException();
        }
        p.setId(id);
        positionRepository.save(p);
        PositionDTO pDTO = new PositionDTO(
                p.getName(),
                p.getRate()
        );
        return pDTO;
    }
    public void delete(long id) {
        if (id <= 0) {
            throw new GeneraleException.InvalidIdException();
        }
        Optional<PositionModel> Delete = positionRepository.findById(id);
        if (Delete.isEmpty()){
            throw new PositionException.PositionNotFoundException();
        }
        positionRepository.deleteById(id);
    }
}
