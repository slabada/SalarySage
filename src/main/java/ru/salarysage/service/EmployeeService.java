package ru.salarysage.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PositionException;
import ru.salarysage.models.*;
import ru.salarysage.repository.EmployeeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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


    public Set<EmployeeModel> check(ProjectModel p) {

        Set<EmployeeModel> result = new HashSet<>();

        if (p.getEmployees() != null) {

            // Извлекаем идентификаторы льгот из расчетного листка.
            List<Long> b = p.getEmployees().stream()
                    .map(EmployeeModel::getId)
                    .toList();

            // Извлекаем соответствующие льготы из базы данных и добавляем их в результат.
            List<EmployeeModel> bDb = employeeRepository.findAllById(b);
            result.addAll(bDb);
        }

        return result;
    }
}
