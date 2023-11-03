package ru.salarysage.salarysage.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.salarysage.salarysage.exception.EmployeeException;
import ru.salarysage.salarysage.exception.GeneraleException;
import ru.salarysage.salarysage.exception.PositionException;
import ru.salarysage.salarysage.models.EmployeeModel;
import ru.salarysage.salarysage.models.PositionModel;
import ru.salarysage.salarysage.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    protected final EmployeeRepository employeeRepository;
    protected final PositionService positionService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           PositionService positionService) {
        this.employeeRepository = employeeRepository;
        this.positionService = positionService;
    }

    // Метод для создания нового сотрудника.
    public EmployeeModel create(EmployeeModel e){

        // Получение информации о должности с использованием PositionService.
        Optional<PositionModel> p = positionService.get(e.getPosition().getId());

        // Проверка, была ли найдена должность.
        if(p.isEmpty()) throw new PositionException.PositionNotFoundException();

        // Установка найденной должности для сотрудника и сохранение его в репозитории.
        e.setPosition(p.get());

        employeeRepository.save(e);

        return e;
    }

    // Метод для получения информации о сотруднике по идентификатору.
    public Optional<EmployeeModel> get(long id){

        // Проверка, является ли предоставленный идентификатор валидным.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Получение информации о сотруднике с использованием репозитория.
        Optional<EmployeeModel> e = employeeRepository.findById(id);

        // Проверка, был ли найден сотрудник с данным идентификатором.
        if(e.isEmpty()) throw new EmployeeException.EmployeeNotFoundException();

        return e;
    }

    // Метод для обновления информации о сотруднике.
    public EmployeeModel put(long id, EmployeeModel e){

        // Проверка, является ли предоставленный идентификатор валидным.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Получение информации о сотруднике с использованием репозитория.
        Optional<EmployeeModel> eDb = employeeRepository.findById(id);

        // Проверка, был ли найден сотрудник с данным идентификатором.
        if(eDb.isEmpty()) throw new EmployeeException.EmployeeNotFoundException();

        // Получение информации о должности с использованием PositionService.
        Optional<PositionModel> pDb = positionService.get(e.getPosition().getId());

        // Проверка, была ли найдена должность.
        if(pDb.isEmpty()) throw new PositionException.PositionNotFoundException();

        // Установка идентификатора для сотрудника и обновление информации в репозитории.
        e.setId(id);
        e.setPosition(pDb.get());

        employeeRepository.save(e);

        return e;
    }

    // Метод для удаления сотрудника по идентификатору.
    public void delete(long id){

        // Проверка, является ли предоставленный идентификатор валидным.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Получение информации о сотруднике с использованием репозитория.
        Optional<EmployeeModel> e = employeeRepository.findById(id);

        // Проверка, был ли найден сотрудник с данным идентификатором.
        if(e.isEmpty()) throw new EmployeeException.EmployeeNotFoundException();

        // Удаление сотрудника из репозитория.
        employeeRepository.deleteById(id);
    }

    // Метод для поиска сотрудников с заданными параметрами и настройкой пагинации.
    public List<EmployeeModel> search(EmployeeModel e, int from, int size){

        // Проверка, что параметры страницы (from и size) являются валидными.
        if(from < 0 || size <= 0) throw new EmployeeException.InvalidPageSizeException();

        // Создание объекта PageRequest для настройки пагинации.
        PageRequest page = PageRequest.of(from, size);

        // Выполнение поиска сотрудников с использованием репозитория.
        return employeeRepository.search(e, page);
    }
}
