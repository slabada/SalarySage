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

    public TimeSheetModel create(TimeSheetModel t){
        boolean tDb = timeSheetRepository.existsByDateAndEmployeeId(t.getDate(), t.getEmployeeId());
        if(tDb){
            throw new TimeSheetException.DateException();
        }
        Optional<EmployeeModel> e = employeeService.get(t.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new  EmployeeException.EmployeeNotFoundException();
        }
        t.setEmployeeId(e.get());
        timeSheetRepository.save(t);
        return t;
    }
    public Optional<TimeSheetModel> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<TimeSheetModel> t = timeSheetRepository.findById(id);
        if(t.isEmpty()){
            throw new TimeSheetException.NullTimeSheetException();
        }
        return t;
    }
    public TimeSheetModel put(long id, TimeSheetModel t){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<TimeSheetModel> tDb = timeSheetRepository.findById(id);
        if(tDb.isEmpty()){
            throw new TimeSheetException.NullTimeSheetException();
        }
        boolean dDb = timeSheetRepository.existsByDateAndEmployeeIdAndIdNot(t.getDate(), t.getEmployeeId(), id);
        if(dDb){
            throw new TimeSheetException.DateException();
        }
        Optional<EmployeeModel> e = employeeService.get(t.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new  EmployeeException.EmployeeNotFoundException();
        }
        t.setId(id);
        t.setEmployeeId(e.get());
        timeSheetRepository.save(t);
        return t;
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<TimeSheetModel> tDb = timeSheetRepository.findById(id);
        if(tDb.isEmpty()){
            throw new TimeSheetException.NullTimeSheetException();
        }
        timeSheetRepository.deleteById(id);
    }
    public List<TimeSheetModel> searchByYearAndMonth(long id, Integer year, Byte month){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        if(year == null){
            year = LocalDate.now().getYear();
        }
        if(month == null){
            month = (byte) LocalDate.now().getMonth().getValue();
        }
        Optional<EmployeeModel> e = employeeService.get(id);
        if(e.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        // Поиск записей о рабочем времени для данного сотрудника, года и месяца.
        List<TimeSheetModel> t = timeSheetRepository.findAllByYearAndMonth(year, month, id);
        if(t.isEmpty()) {
            throw new TimeSheetException.NullTimeSheetException();
        }
        return t;
    }
    public List<TimeSheetModel> getAll(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        List<TimeSheetModel> t = timeSheetRepository.findAllByEmployeeId_Id(id);
        if(t.isEmpty()){
            throw new TimeSheetException.NullTimeSheetException();
        }
        return t;
    }
}
