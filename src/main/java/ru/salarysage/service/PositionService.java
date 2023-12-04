package ru.salarysage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.salarysage.dto.PositionDTO;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.PositionModel;
import ru.salarysage.repository.PositionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionService {
    protected final PositionRepository positionRepository;
    protected final GenericMapper genericMapper;

    public PositionDTO create(PositionModel p) {
        boolean pByName = positionRepository.existsByName(p.getName());
        if (pByName) {
            throw new PositionException.PositionAlreadyExistsException();
        }
        PositionModel save = positionRepository.save(p);
        return genericMapper.convertToDto(save,PositionDTO.class);
    }
    public Optional<PositionDTO> get(long id) {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        PositionModel p = positionRepository.findById(id)
                .orElseThrow(PositionException.PositionNotFoundException::new);
        PositionDTO pDTO = genericMapper.convertToDto(p,PositionDTO.class);
        return Optional.of(pDTO);
    }
    public PositionDTO put(long id, PositionModel p) {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        positionRepository.findById(id)
                .orElseThrow(PositionException.PositionNotFoundException::new);
        boolean nDb = positionRepository.existsByNameAndIdNot(p.getName(), id);
        if(nDb){
            throw new PositionException.PositionAlreadyExistsException();
        }
        p.setId(id);
        PositionModel save = positionRepository.save(p);
        return genericMapper.convertToDto(save,PositionDTO.class);
    }
    public void delete(long id) {
        if (id <= 0) {
            throw new GeneraleException.InvalidIdException();
        }
        positionRepository.findById(id)
                .orElseThrow(PositionException.PositionNotFoundException::new);
        positionRepository.deleteById(id);
    }
}
