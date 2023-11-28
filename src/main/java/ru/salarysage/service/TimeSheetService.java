package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.dto.TimeSheetDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.TimeSheetException;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.PositionModel;
import ru.salarysage.models.TimeSheetModel;
import ru.salarysage.repository.TimeSheetRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TimeSheetService {
    protected final TimeSheetRepository timeSheetRepository;
    protected final EmployeeService employeeService;
    public TimeSheetService(TimeSheetRepository timeSheetRepository,
                            EmployeeService employeeService) {
        this.timeSheetRepository = timeSheetRepository;
        this.employeeService = employeeService;
    }

    public TimeSheetDTO create(TimeSheetModel t){
        boolean tDb = timeSheetRepository.existsByDateAndEmployeeId(t.getDate(), t.getEmployeeId());
        if(tDb){
            throw new TimeSheetException.DateException();
        }
        Optional<EmployeeDTO> e = employeeService.get(t.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new  EmployeeException.EmployeeNotFoundException();
        }
        t.setEmployeeId(new EmployeeModel(t.getEmployeeId().getId()));
        timeSheetRepository.save(t);
        TimeSheetDTO tDTO = new TimeSheetDTO(
                t.getDate(),
                e.get(),
                t.getHoursWorked(),
                t.isHoliday(),
                t.isMedical(),
                t.isVacation(),
                t.getNotes()
        );
        return tDTO;
    }
    public Optional<TimeSheetDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<TimeSheetModel> t = timeSheetRepository.findById(id);
        if(t.isEmpty()){
            throw new TimeSheetException.NullTimeSheetException();
        }
        Optional<EmployeeDTO> e = employeeService.get(t.get().getEmployeeId().getId());
        if(e.isEmpty()){
            throw new  EmployeeException.EmployeeNotFoundException();
        }
        TimeSheetDTO tDTO = new TimeSheetDTO(
                t.get().getDate(),
                e.get(),
                t.get().getHoursWorked(),
                t.get().isHoliday(),
                t.get().isMedical(),
                t.get().isVacation(),
                t.get().getNotes()
        );
        return Optional.of(tDTO);
    }
    public TimeSheetDTO put(long id, TimeSheetModel t){
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
        Optional<EmployeeDTO> e = employeeService.get(t.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new  EmployeeException.EmployeeNotFoundException();
        }
        t.setId(id);
        t.setEmployeeId(new EmployeeModel(t.getEmployeeId().getId()));
        timeSheetRepository.save(t);
        TimeSheetDTO tDTO = new TimeSheetDTO(
                t.getDate(),
                e.get(),
                t.getHoursWorked(),
                t.isHoliday(),
                t.isMedical(),
                t.isVacation(),
                t.getNotes()
        );
        return tDTO;
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
    public List<TimeSheetDTO> searchByYearAndMonth(long id, Integer year, Byte month){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        if(year == null){
            year = LocalDate.now().getYear();
        }
        if(month == null){
            month = (byte) LocalDate.now().getMonth().getValue();
        }
        Optional<EmployeeDTO> e = employeeService.get(id);
        if(e.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        // Поиск записей о рабочем времени для данного сотрудника, года и месяца.
        List<TimeSheetModel> t = timeSheetRepository.findAllByYearAndMonth(year, month, id);
        if(t.isEmpty()) {
            throw new TimeSheetException.NullTimeSheetException();
        }
        List<TimeSheetDTO> tDTO = t.stream()
                .map(timeSheetModel -> new TimeSheetDTO(
                        timeSheetModel.getDate(),
                        e.get(),
                        timeSheetModel.getHoursWorked(),
                        timeSheetModel.isHoliday(),
                        timeSheetModel.isMedical(),
                        timeSheetModel.isVacation(),
                        timeSheetModel.getNotes()
                ))
                .toList();
        return tDTO;
    }
    public List<TimeSheetDTO> getAll(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        List<TimeSheetDTO> t = timeSheetRepository.findAllByEmployeeId_Id(id);
        if(t.isEmpty()){
            throw new TimeSheetException.NullTimeSheetException();
        }
        return t;
    }
}
