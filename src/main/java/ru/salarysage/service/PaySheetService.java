package ru.salarysage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.salarysage.dto.PaySheetDTO;
import ru.salarysage.dto.TimeSheetDTO;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PaySheetException;
import ru.salarysage.mapper.GenericMapper;
import ru.salarysage.models.BenefitModel;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.PaySheetModel;
import ru.salarysage.models.RateModel;
import ru.salarysage.repository.EmployeeRepository;
import ru.salarysage.repository.PaySheetRepository;
import ru.salarysage.util.CalculationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaySheetService {

    protected final PaySheetRepository paySheetRepository;
    protected final EmployeeRepository employeeRepository;
    protected final RateService rateService;
    protected final BenefitService benefitService;
    protected final TimeSheetService timeSheetService;
    protected final CalculationUtil calculationUtil;
    protected final GenericMapper genericMapper;

    public PaySheetDTO create(PaySheetModel ps){
        EmployeeModel e = employeeRepository.findById(ps.getEmployeeId().getId())
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        ps.setEmployeeId(e);
        // Поиск записей о рабочем времени для данного сотрудника и месяца.
        List<TimeSheetDTO> t = timeSheetService.searchByYearAndMonth(
                ps.getEmployeeId().getId(),
                ps.getYear(),
                (byte) ps.getMonth()
        );
        if(t.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        // Проверка льгот для расчетного листка и установка их в расчетный листок.
        Set<BenefitModel> bc = benefitService.check(ps);
        ps.setBenefit(bc);
        // Проверка налог для расчетного листка и установка их в расчетный листок.
        Set<RateModel> rc = rateService.check(ps);
        ps.setRate(rc);
        // Вычисление общей суммы для расчетного листка.
        BigDecimal total = calculationUtil.calculationTotal(
                t,
                e.getPosition(),
                ps
        );
        ps.setTotalAmount(total);
        PaySheetModel save = paySheetRepository.save(ps);
        return genericMapper.convertToDto(save, PaySheetDTO.class);
    }
    public Optional<PaySheetDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        PaySheetModel ps = paySheetRepository.findById(id)
                .orElseThrow(PaySheetException.PaySheetNotFount::new);
        PaySheetDTO psDTO = genericMapper.convertToDto(ps, PaySheetDTO.class);
        return Optional.of(psDTO);
    }
    public PaySheetDTO put(long id, PaySheetModel ps){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        paySheetRepository.findById(id)
                .orElseThrow(PaySheetException.PaySheetNotFount::new);
        EmployeeModel e = employeeRepository.findById(ps.getEmployeeId().getId())
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        // Поиск записей о рабочем времени для данного сотрудника и месяца.
        List<TimeSheetDTO> t = timeSheetService.searchByYearAndMonth(
                ps.getEmployeeId().getId(),
                ps.getYear(),
                (byte) ps.getMonth()
        );
        if(t.isEmpty()) {
            throw new PaySheetException.PaySheetNotFount();
        }
        // Проверка льгот для расчетного листка и установка их в расчетный листок.
        Set<BenefitModel> bc = benefitService.check(ps);
        ps.setBenefit(bc);
        // Проверка налог для расчетного листка и установка их в расчетный листок.
        Set<RateModel> rc = rateService.check(ps);
        ps.setRate(rc);
        // Вычисление общей суммы для расчетного листка.
        BigDecimal total = calculationUtil.calculationTotal(
                t,
                e.getPosition(),
                ps
        );
        ps.setId(id);
        ps.setTotalAmount(total);
        PaySheetModel save = paySheetRepository.save(ps);
        return genericMapper.convertToDto(save, PaySheetDTO.class);
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
       paySheetRepository.findById(id)
               .orElseThrow(PaySheetException.PaySheetNotFount::new);
        paySheetRepository.deleteById(id);
    }
    public List<PaySheetDTO> getAll(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        // Поиск расчетных листков для данного сотрудника.
        List<PaySheetModel> ps = paySheetRepository.findAllByEmployeeId_Id(id);
        if(ps.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        return ps.stream()
                .map(timeSheetModel -> genericMapper.convertToDto(
                        timeSheetModel,
                        PaySheetDTO.class)
                ).toList();
    }
}
