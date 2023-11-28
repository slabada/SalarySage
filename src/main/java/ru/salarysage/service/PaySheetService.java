package ru.salarysage.service;

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
public class PaySheetService {
    protected final PaySheetRepository paySheetRepository;
    protected final EmployeeRepository employeeRepository;
    protected final RateService rateService;
    protected final BenefitService benefitService;
    protected final TimeSheetService timeSheetService;
    protected final CalculationUtil calculationUtil;

    protected final GenericMapper genericMapper;

    public PaySheetService(PaySheetRepository paySheetRepository,
                           EmployeeRepository employeeRepository,
                           RateService rateService,
                           BenefitService benefitService,
                           TimeSheetService timeSheetService,
                           CalculationUtil calculationUtil,
                           GenericMapper genericMapper) {
        this.paySheetRepository = paySheetRepository;
        this.employeeRepository = employeeRepository;
        this.rateService = rateService;
        this.benefitService = benefitService;
        this.timeSheetService = timeSheetService;
        this.calculationUtil = calculationUtil;
        this.genericMapper = genericMapper;
    }

    public PaySheetDTO create(PaySheetModel ps){
        Optional<EmployeeModel> e = employeeRepository.findById(ps.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        ps.setEmployeeId(e.get());
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
                e.get().getPosition(),
                ps
        );
        ps.setTotalAmount(total);
        PaySheetModel save = paySheetRepository.save(ps);
        PaySheetDTO psDTO = genericMapper.convertToDto(save, PaySheetDTO.class);
        return psDTO;
    }
    public Optional<PaySheetDTO> get(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<PaySheetModel> ps = paySheetRepository.findById(id);
        if(ps.isEmpty()) {
            throw new PaySheetException.PaySheetNotFount();
        }
        PaySheetDTO psDTO = genericMapper.convertToDto(ps, PaySheetDTO.class);
        return Optional.of(psDTO);
    }
    public PaySheetDTO put(long id, PaySheetModel ps){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        Optional<PaySheetModel> psDb = paySheetRepository.findById(id);
        if(psDb.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        Optional<EmployeeModel> e = employeeRepository.findById(ps.getEmployeeId().getId());
        if(e.isEmpty()){
            throw new EmployeeException.EmployeeNotFoundException();
        }
        ps.setEmployeeId(new EmployeeModel(ps.getEmployeeId().getId()));
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
                e.get().getPosition(),
                ps
        );
        ps.setId(id);
        ps.setTotalAmount(total);
        PaySheetModel save = paySheetRepository.save(ps);
        PaySheetDTO psDTO = genericMapper.convertToDto(save, PaySheetDTO.class);
        return psDTO;
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
    public List<PaySheetDTO> getAll(long id){
        if(id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        // Поиск расчетных листков для данного сотрудника.
        List<PaySheetModel> ps = paySheetRepository.findAllByEmployeeId_Id(id);
        if(ps.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        List<PaySheetDTO> psDTO = ps.stream()
                .map(timeSheetModel -> genericMapper.convertToDto(
                        timeSheetModel,
                        PaySheetDTO.class
                        )
                ).toList();
        return psDTO;
    }
}
