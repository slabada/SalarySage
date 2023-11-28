package ru.salarysage.service;

import org.springframework.stereotype.Service;
import ru.salarysage.dto.*;
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
import java.util.stream.Collectors;

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

    public PaySheetDTO create(PaySheetModel ps){
        Optional<EmployeeDTO> e = employeeService.get(ps.getEmployeeId().getId());
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
        if(t.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        // Проверка льгот для расчетного листка и установка их в расчетный листок.
        Set<BenefitModel> bc = benefitService.check(ps);
        ps.setBenefit(bc);
        Set<BenefitDTO> bcDTO = bc.stream()
                .map(benefitModel -> new BenefitDTO(
                        benefitModel.getName(),
                        benefitModel.getAmount()
                ))
                .collect(Collectors.toSet());
        // Проверка налог для расчетного листка и установка их в расчетный листок.
        Set<RateModel> rc = rateService.check(ps);
        ps.setRate(rc);
        Set<RateDTO> rcDTO = rc.stream()
                .map(rateModel -> new RateDTO(
                        rateModel.getName(),
                        rateModel.getPercent()
                ))
                .collect(Collectors.toSet());
        // Вычисление общей суммы для расчетного листка.
        BigDecimal total = calculationUtil.calculationTotal(
                t,
                e.get().getPosition(),
                ps
        );
        ps.setTotalAmount(total);
        paySheetRepository.save(ps);
        PaySheetDTO psDTO = new PaySheetDTO(
                ps.getYear(),
                ps.getMonth(),
                e.get(),
                bcDTO,
                rcDTO,
                ps.getTotalAmount()
        );
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

        PaySheetDTO psDTO = new PaySheetDTO(
                ps.get().getYear(),
                ps.get().getMonth(),
                new EmployeeDTO(
                        ps.get().getEmployeeId().getFirstName(),
                        ps.get().getEmployeeId().getLastName(),
                        ps.get().getEmployeeId().getAddress(),
                        new PositionDTO(
                                ps.get().getEmployeeId().getPosition().getName(),
                                ps.get().getEmployeeId().getPosition().getRate()
                        )
                ),
                ps.get().getBenefit().stream()
                                .map(benefitModel -> new BenefitDTO(
                                        benefitModel.getName(),
                                        benefitModel.getAmount()
                                )).collect(Collectors.toSet()),
                ps.get().getRate().stream()
                        .map(rateModel -> new RateDTO(
                                rateModel.getName(),
                                rateModel.getPercent()
                        )).collect(Collectors.toSet()),
                ps.get().getTotalAmount()
        );
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
        Optional<EmployeeDTO> e = employeeService.get(ps.getEmployeeId().getId());
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
        Set<BenefitDTO> bcDTO = bc.stream()
                .map(benefitModel -> new BenefitDTO(
                        benefitModel.getName(),
                        benefitModel.getAmount()
                ))
                .collect(Collectors.toSet());
        // Проверка налог для расчетного листка и установка их в расчетный листок.
        Set<RateModel> rc = rateService.check(ps);
        ps.setRate(rc);
        Set<RateDTO> rcDTO = rc.stream()
                .map(rateModel -> new RateDTO(
                        rateModel.getName(),
                        rateModel.getPercent()
                ))
                .collect(Collectors.toSet());
        // Вычисление общей суммы для расчетного листка.
        BigDecimal total = calculationUtil.calculationTotal(
                t,
                e.get().getPosition(),
                ps
        );
        ps.setId(id);
        ps.setTotalAmount(total);
        paySheetRepository.save(ps);
        PaySheetDTO psDTO = new PaySheetDTO(
                ps.getYear(),
                ps.getMonth(),
                e.get(),
                bcDTO,
                rcDTO,
                ps.getTotalAmount()
        );
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
                .map(paySheetModel -> new PaySheetDTO(
                        paySheetModel.getYear(),
                        paySheetModel.getMonth(),
                        new EmployeeDTO(
                                paySheetModel.getEmployeeId().getFirstName(),
                                paySheetModel.getEmployeeId().getLastName(),
                                paySheetModel.getEmployeeId().getAddress(),
                                new PositionDTO(
                                        paySheetModel.getEmployeeId().getPosition().getName(),
                                        paySheetModel.getEmployeeId().getPosition().getRate()
                                )
                        ),
                        paySheetModel.getBenefit().stream()
                                .map(benefitModel -> new BenefitDTO(
                                        benefitModel.getName(),
                                        benefitModel.getAmount()
                                )).collect(Collectors.toSet()),
                        paySheetModel.getRate().stream()
                                .map(rateModel -> new RateDTO(
                                        rateModel.getName(),
                                        rateModel.getPercent()
                                )).collect(Collectors.toSet()),
                        paySheetModel.getTotalAmount()
                )).toList();
        return psDTO;
    }
}
