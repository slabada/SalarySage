package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.TimeSheetDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.TimeSheetException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.TimeSheetModel;
import ru.salarysage.repository.EmployeeRepository;
import ru.salarysage.repository.TimeSheetRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TimeSheetService {
    protected final TimeSheetRepository timeSheetRepository;
    protected final EmployeeRepository employeeRepository;

    protected final GenericMapper genericMapper;
    public TimeSheetService(TimeSheetRepository timeSheetRepository,
                            EmployeeRepository employeeRepository,
                            GenericMapper genericMapper) {
        this.timeSheetRepository = timeSheetRepository;
        this.employeeRepository = employeeRepository;
        this.genericMapper = genericMapper;
    }

    public TimeSheetDTO create(TimeSheetModel t){
        boolean tDb = timeSheetRepository.existsByDateAndEmployeeId(t.getDate(), t.getEmployeeId());
        if(tDb){
            throw new TimeSheetException.DateException();
        }
        Optional<EmployeeModel> e = employeeRepository.findById(t.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new  EmployeeException.EmployeeNotFoundException();
        }
        t.setEmployeeId(e.get());
        TimeSheetModel save = timeSheetRepository.save(t);
        TimeSheetDTO tDTO = genericMapper.convertToDto(save, TimeSheetDTO.class);
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
        Optional<EmployeeModel> e = employeeRepository.findById(t.get().getEmployeeId().getId());
        if(e.isEmpty()){
            throw new  EmployeeException.EmployeeNotFoundException();
        }
        TimeSheetDTO tDTO = genericMapper.convertToDto(t, TimeSheetDTO.class);
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
        Optional<EmployeeModel> e = employeeRepository.findById(t.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new  EmployeeException.EmployeeNotFoundException();
        }
        t.setId(id);
        t.setEmployeeId(e.get());
        TimeSheetModel save = timeSheetRepository.save(t);
        TimeSheetDTO tDTO = genericMapper.convertToDto(save, TimeSheetDTO.class);
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
        Optional<EmployeeModel> e = employeeRepository.findById(id);
        if(e.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        // Поиск записей о рабочем времени для данного сотрудника, года и месяца.
        List<TimeSheetModel> t = timeSheetRepository.findAllByYearAndMonth(year, month, id);
        if(t.isEmpty()) {
            throw new TimeSheetException.NullTimeSheetException();
        }
        List<TimeSheetDTO> tDTO = t.stream()
                .map(timeSheetModel -> genericMapper.convertToDto(timeSheetModel, TimeSheetDTO.class)
                ).toList();
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
