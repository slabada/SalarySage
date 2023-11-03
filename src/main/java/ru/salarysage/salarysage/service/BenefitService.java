package ru.salarysage.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.salarysage.exception.BenefitException;
import ru.salarysage.salarysage.exception.GeneraleException;
import ru.salarysage.salarysage.models.BenefitModel;
import ru.salarysage.salarysage.models.PaySheetModel;
import ru.salarysage.salarysage.repository.BenefitRepository;

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

    // Метод для создания новой льготы.
    public BenefitModel create(BenefitModel b){

        // Проверка, существует ли льгота с таким именем в базе данных.
        boolean nDb = benefitRepository.existsByName(b.getName());

        // Если льгота с таким именем уже существует, выбрасываем исключение.
        if(nDb) throw new BenefitException.BenefitAlreadyExistsException();

        // Сохраняем новую льготу в базе данных.
        benefitRepository.save(b);

        return b;
    }

    // Метод для получения льготы по её идентификатору.
    public Optional<BenefitModel> get(long id){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск льготы в базе данных по идентификатору.
        Optional<BenefitModel> bDb = benefitRepository.findById(id);

        // Если льгота не найдена, выбрасываем исключение.
        if(bDb.isEmpty()) throw new BenefitException.NullBenefitException();

        return bDb;
    }

    // Метод для обновления существующей льготы.
    public BenefitModel put (long id, BenefitModel b){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск существующей льготы в базе данных по идентификатору.
        Optional<BenefitModel> bDb = benefitRepository.findById(id);

        // Если льгота не найдена, выбрасываем исключение.
        if(bDb.isEmpty()) throw new BenefitException.NullBenefitException();

        // Проверка, существует ли другая льгота с таким же именем.
        boolean nDb = benefitRepository.existsByNameAndIdNot(b.getName(), id);

        // Если льгота с таким именем уже существует, выбрасываем исключение.
        if(nDb) throw new BenefitException.BenefitAlreadyExistsException();

        // Устанавливаем идентификатор льготы и сохраняем обновленные данные в базе данных.
        b.setId(id);
        benefitRepository.save(b);

        return b;
    }

    // Метод для удаления льготы по её идентификатору.
    public void delete(long id){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск льготы в базе данных по идентификатору.
        Optional<BenefitModel> bDb = benefitRepository.findById(id);

        // Если льгота не найдена, выбрасываем исключение.
        if(bDb.isEmpty()) throw new BenefitException.NullBenefitException();

        // Удаляем льготу из базы данных.
        benefitRepository.deleteById(id);
    }

    // Метод для проверки льгот, связанных с расчетным листком.
    public Set<BenefitModel> check(PaySheetModel pc) {

        Set<BenefitModel> result = new HashSet<>();

        if (pc.getBenefit() != null) {

            // Извлекаем идентификаторы льгот из расчетного листка.
            List<Long> b = pc.getBenefit().stream()
                    .map(BenefitModel::getId)
                    .toList();

            // Извлекаем соответствующие льготы из базы данных и добавляем их в результат.
            List<BenefitModel> bDb = benefitRepository.findAllById(b);
            result.addAll(bDb);
        }

        return result;
    }
}
