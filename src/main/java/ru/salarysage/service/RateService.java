package ru.salarysage.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.RateException;
import ru.salarysage.models.PaySheetModel;
import ru.salarysage.models.RateModel;
import ru.salarysage.repository.RateRepository;
import ru.salarysage.event.CreateRateEvent;

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

    @EventListener()
    public void createStartRate(CreateRateEvent event) {
        // Создание объекта начальной ставки
        RateModel startRate = new RateModel(1, "НДФЛ", 13);

        // Поиск начальной ставки по имени
        boolean rDb = rateRepository.existsByName(startRate.getName());

        // Если начальная ставка не существует, она сохраняется
        if (!rDb) rateRepository.save(startRate);
    }

    // Метод для создания новой ставки.
    public RateModel create(RateModel r){

        // Проверка, существует ли ставка с таким именем в базе данных.
        boolean nBd = rateRepository.existsByName(r.getName());

        // Если ставка с таким именем уже существует, выбрасываем исключение.
        if(nBd) throw new RateException.RateAlreadyExistsException();

        // Сохраняем новую ставку в базе данных.
        rateRepository.save(r);

        return r;
    }

    // Метод для получения ставки по её идентификатору.
    public Optional<RateModel> get(long id){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск ставки в базе данных по идентификатору.
        Optional<RateModel> r = rateRepository.findById(id);

        // Если ставка не найдена, выбрасываем исключение.
        if(r.isEmpty()) throw new RateException.NullRateException();

        return r;
    }

    // Метод для обновления существующей ставки.
    public RateModel put(long id, RateModel r){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск существующей ставки в базе данных по идентификатору.
        Optional<RateModel> rDb = rateRepository.findById(id);

        // Если ставка не найдена, выбрасываем исключение.
        if(rDb.isEmpty()) throw new RateException.NullRateException();

        // Проверка, существует ли другая ставка с таким же именем.
        boolean nBd = rateRepository.existsByNameAndIdNot(r.getName(), id);

        // Если ставка с таким именем уже существует, выбрасываем исключение.
        if(nBd) throw new RateException.RateAlreadyExistsException();

        // Устанавливаем идентификатор ставки и сохраняем обновленные данные в базе данных.
        r.setId(id);
        rateRepository.save(r);

        return r;
    }

    // Метод для удаления ставки по её идентификатору.
    public void delete(long id){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск ставки в базе данных по идентификатору.
        Optional<RateModel> rDb = rateRepository.findById(id);

        // Если ставка не найдена, выбрасываем исключение.
        if(rDb.isEmpty()) throw new RateException.NullRateException();

        // Удаление ставки из базе данных.
        rateRepository.deleteById(id);
    }

    // Метод Check для проверки налоговых ставок в объекте PaySheet
    public Set<RateModel> check(PaySheetModel prc) {

        Set<RateModel> result = new HashSet<>();

        if (prc.getRate() != null && prc.getRate().size() > 0) {

            // Поиск налоговой ставки в репозитории по идентификатору
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
