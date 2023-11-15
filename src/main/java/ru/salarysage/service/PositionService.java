package ru.salarysage.service;

import org.springframework.stereotype.Service;
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

    // Метод для создания новой должности.
    public PositionModel create(PositionModel p) {

        // Проверка, существует ли должность с таким именем.
        boolean pByName = positionRepository.existsByName(p.getName());

        // Если должность с таким именем уже существует, выбрасываем исключение.
        if (pByName) throw new PositionException.PositionAlreadyExistsException();

        // Сохранение новой должности в репозитории.
        positionRepository.save(p);

        return p;
    }

    // Метод для получения информации о должности по идентификатору.
    public Optional<PositionModel> get(long id) {

        // Проверка, является ли предоставленный идентификатор валидным.
        if (id <= 0) throw new GeneraleException.InvalidIdException();

        // Получение информации о должности с использованием репозитория.
        Optional<PositionModel> p = positionRepository.findById(id);

        // Проверка, была ли найдена должность с данным идентификатором.
        if (p.isEmpty()) throw new PositionException.PositionNotFoundException();

        return p;
    }

    // Метод для обновления информации о должности.
    public PositionModel put(long id, PositionModel p) {

        // Проверка, является ли предоставленный идентификатор валидным.
        if (id <= 0) throw new GeneraleException.InvalidIdException();

        // Получение информации о существующей должности с использованием репозитория.
        Optional<PositionModel> pDb = positionRepository.findById(id);

        // Проверка, была ли найдена должность с данным идентификатором.
        if (pDb.isEmpty()) throw new PositionException.PositionNotFoundException();

        boolean nDb = positionRepository.existsByNameAndIdNot(p.getName(), id);

        if(nDb) throw new PositionException.PositionAlreadyExistsException();

        // Установка идентификатора для должности и обновление информации в репозитории.
        p.setId(id);

        positionRepository.save(p);

        return p;
    }

    // Метод для удаления должности по идентификатору.
    public void delete(long id) {

        // Проверка, является ли предоставленный идентификатор валидным.
        if (id <= 0) throw new GeneraleException.InvalidIdException();

        // Получение информации о должности с использованием репозитория.
        Optional<PositionModel> Delete = positionRepository.findById(id);

        // Проверка, была ли найдена должность с данным идентификатором.
        if (Delete.isEmpty()) throw new PositionException.PositionNotFoundException();

        // Удаление должности из репозитория.
        positionRepository.deleteById(id);
    }
}
