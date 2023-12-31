package ru.salarysage.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TimeSheetService {

    protected final TimeSheetRepository timeSheetRepository;
    protected final EmployeeRepository employeeRepository;
    protected final GenericMapper genericMapper;

    public TimeSheetDTO create(TimeSheetModel t){
        boolean tDb = timeSheetRepository.existsByDateAndEmployeeId(t.getDate(), t.getEmployeeId());
        if(tDb){
            throw new TimeSheetException.DateException();
        }
        EmployeeModel e = employeeRepository.findById(t.getEmployeeId().getId())
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        t.setEmployeeId(e);
        TimeSheetModel save = timeSheetRepository.save(t);
        return genericMapper.convertToDto(save, TimeSheetDTO.class);
    }
    public Optional<TimeSheetDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        TimeSheetModel t = timeSheetRepository.findById(id)
                .orElseThrow(TimeSheetException.NullTimeSheetException::new);
        TimeSheetDTO tDTO = genericMapper.convertToDto(t, TimeSheetDTO.class);
        return Optional.of(tDTO);
    }
    public TimeSheetDTO put(long id, TimeSheetModel t){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        timeSheetRepository.findById(id)
                .orElseThrow(TimeSheetException.NullTimeSheetException::new);
        boolean dDb = timeSheetRepository.existsByDateAndEmployeeIdAndIdNot(t.getDate(), t.getEmployeeId(), id);
        if(dDb){
            throw new TimeSheetException.DateException();
        }
        EmployeeModel e = employeeRepository.findById(t.getEmployeeId().getId())
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        t.setId(id);
        t.setEmployeeId(e);
        TimeSheetModel save = timeSheetRepository.save(t);
        return genericMapper.convertToDto(save, TimeSheetDTO.class);
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        timeSheetRepository.findById(id)
                .orElseThrow(TimeSheetException.NullTimeSheetException::new);
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
        employeeRepository.findById(id)
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        // Поиск записей о рабочем времени для данного сотрудника, года и месяца.
        List<TimeSheetModel> t = timeSheetRepository.findAllByYearAndMonth(year, month, id);
        if(t.isEmpty()) {
            throw new TimeSheetException.NullTimeSheetException();
        }
        return t.stream()
                .map(timeSheetModel -> genericMapper.convertToDto(timeSheetModel, TimeSheetDTO.class)
                ).toList();
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
