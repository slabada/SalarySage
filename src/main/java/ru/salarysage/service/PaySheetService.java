package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.exception.EmployeeException;
import ru.salarysage.exception.GeneraleException;
import ru.salarysage.exception.PaySheetException;
import ru.salarysage.models.*;
import ru.salarysage.repository.PaySheetRepository;
import ru.salarysage.util.CalculationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PaySheetService {
    protected final PaySheetRepository paySheetRepository;
    protected final EmployeeService employeeService;
    protected final RateService rateService;
    protected final BenefitService benefitService;
    protected final TimeSheetService timeSheetService;
    protected final CalculationUtil calculationUtil;

    public PaySheetService(PaySheetRepository paySheetRepository,
                           EmployeeService employeeService,
                           RateService rateService,
                           BenefitService benefitService,
                           TimeSheetService timeSheetService,
                           CalculationUtil calculationUtil) {
        this.paySheetRepository = paySheetRepository;
        this.employeeService = employeeService;
        this.rateService = rateService;
        this.benefitService = benefitService;
        this.timeSheetService = timeSheetService;
        this.calculationUtil = calculationUtil;
    }

    public PaySheetModel create(PaySheetModel ps){
        Optional<EmployeeModel> e = employeeService.get(ps.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        ps.setEmployeeId(e.get());
        // Поиск записей о рабочем времени для данного сотрудника и месяца.
        List<TimeSheetModel> t = timeSheetService.searchByYearAndMonth(
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
                ps.getEmployeeId().getPosition(),
                ps
        );
        ps.setTotalAmount(total);
        paySheetRepository.save(ps);
        return ps;
    }
    public Optional<PaySheetModel> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<PaySheetModel> ps = paySheetRepository.findById(id);
        if(ps.isEmpty()) {
            throw new PaySheetException.PaySheetNotFount();
        }
        return ps;
    }
    public PaySheetModel put(long id, PaySheetModel ps){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<PaySheetModel> psDb = paySheetRepository.findById(id);
        if(psDb.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        Optional<EmployeeModel> e = employeeService.get(ps.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        ps.setEmployeeId(e.get());
        // Поиск записей о рабочем времени для данного сотрудника и месяца.
        List<TimeSheetModel> t = timeSheetService.searchByYearAndMonth(
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
        // Проверка ставок для расчетного листка и установка их в расчетный листок.
        Set<RateModel> rc = rateService.check(ps);
        ps.setRate(rc);
        // Вычисление общей суммы для расчетного листка.
        BigDecimal total = calculationUtil.calculationTotal(
                t,
                ps.getEmployeeId().getPosition(),
                ps
        );
        ps.setId(id);
        ps.setTotalAmount(total);
        paySheetRepository.save(ps);
        return ps;
    }
    public void delete(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<PaySheetModel> ps = paySheetRepository.findById(id);
        if(ps.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        paySheetRepository.deleteById(id);
    }
    public List<PaySheetModel> getAll(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        // Поиск расчетных листков для данного сотрудника.
        List<PaySheetModel> ps = paySheetRepository.findAllByEmployeeId_Id(id);
        if(ps.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        return ps;
    }
}
