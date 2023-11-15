package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.TimeSheetException;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.TimeSheetModel;
import ru.salarysage.repository.TimeSheetRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TimeSheetService {

    protected final TimeSheetRepository timeSheetRepository;

    protected final EmployeeService employeeService;

    public TimeSheetService(TimeSheetRepository timeSheetRepository,
                            EmployeeService employeeService) {
        this.timeSheetRepository = timeSheetRepository;
        this.employeeService = employeeService;
    }

    // Метод для создания записи о рабочем времени.
    public TimeSheetModel create(TimeSheetModel t){

        // Проверка, существует ли запись о рабочем времени для данной даты и сотрудника.
        boolean tDb = timeSheetRepository.existsByDateAndEmployeeId(t.getDate(), t.getEmployeeId());

        // Если запись уже существует, выбрасываем исключение.
        if(tDb) throw new TimeSheetException.DateException();

        // Поиск сотрудника по его идентификатору.
        Optional<EmployeeModel> e = employeeService.get(t.getEmployeeId().getId());

        // Если сотрудник не найден, выбрасываем исключение.
        if(e.isEmpty()) throw new  EmployeeException.EmployeeNotFoundException();

        // Устанавливаем сотрудника для записи о рабочем времени и сохраняем запись.
        t.setEmployeeId(e.get());

        timeSheetRepository.save(t);

        return t;
    }

    // Метод для получения записи о рабочем времени по её идентификатору.
    public Optional<TimeSheetModel> get(long id){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск записи о рабочем времени в базе данных по идентификатору.
        Optional<TimeSheetModel> t = timeSheetRepository.findById(id);

        // Если запись не найдена, выбрасываем исключение.
        if(t.isEmpty()) throw new TimeSheetException.NullTimeSheetException();

        return t;
    }

    // Метод для обновления существующей записи о рабочем времени.
    public TimeSheetModel put(long id, TimeSheetModel t){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск существующей записи о рабочем времени в базе данных по идентификатору.
        Optional<TimeSheetModel> tDb = timeSheetRepository.findById(id);

        // Если запись не найдена, выбрасываем исключение.
        if(tDb.isEmpty()) throw new TimeSheetException.NullTimeSheetException();

        // Проверка, существует ли запись о рабочем времени для данной даты и сотрудника.
        boolean dDb = timeSheetRepository.existsByDateAndEmployeeIdAndIdNot(t.getDate(), t.getEmployeeId(), id);

        // Если запись уже существует, выбрасываем исключение.
        if(dDb) throw new TimeSheetException.DateException();

        // Поиск сотрудника по его идентификатору.
        Optional<EmployeeModel> e = employeeService.get(t.getEmployeeId().getId());

        // Если сотрудник не найден, выбрасываем исключение.
        if(e.isEmpty()) throw new  EmployeeException.EmployeeNotFoundException();

        // Устанавливаем идентификатор записи о рабочем времени и сотрудника, затем сохраняем обновленные данные.
        t.setId(id);
        t.setEmployeeId(e.get());
        timeSheetRepository.save(t);

        return t;
    }

    // Метод для удаления записи о рабочем времени по её идентификатору.
    public void delete(long id){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск записи о рабочем времени в базе данных по идентификатору.
        Optional<TimeSheetModel> tDb = timeSheetRepository.findById(id);

        // Если запись не найдена, выбрасываем исключение.
        if(tDb.isEmpty()) throw new TimeSheetException.NullTimeSheetException();

        // Удаление записи о рабочем времени из базы данных.
        timeSheetRepository.deleteById(id);
    }

    // Метод для поиска записей о рабочем времени по году и месяцу для конкретного сотрудника.
    public List<TimeSheetModel> searchByYearAndMonth(long id, Integer year, Byte month){

        // Проверка, что идентификатор сотрудника положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Если год и месяц не указаны, используем текущий год и месяц.
        if(year == null) year = LocalDate.now().getYear();
        if(month == null) month = (byte) LocalDate.now().getMonth().getValue();

        // Поиск сотрудника по его идентификатору.
        Optional<EmployeeModel> e = employeeService.get(id);

        // Если сотрудник не найден, выбрасываем исключение.
        if(e.isEmpty()) throw new EmployeeException.EmployeeNotFoundException();

        // Поиск записей о рабочем времени для данного сотрудника, года и месяца.
        List<TimeSheetModel> t = timeSheetRepository.findAllByYearAndMonth(year, month, id);

        // Если записи не найдены, выбрасываем исключение.
        if(t.isEmpty()) throw new TimeSheetException.NullTimeSheetException();

        return t;
    }
}
