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

    // Конструктор сервиса, в котором инъектируются зависимости.
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

    // Метод для создания расчетного листка.
    public PaySheetModel create(PaySheetModel ps){

        // Поиск сотрудника по его идентификатору.
        Optional<EmployeeModel> e = employeeService.get(ps.getEmployeeId().getId());

        // Если сотрудник не найден, выбрасываем исключение.
        if(e.isEmpty()) throw new EmployeeException.EmployeeNotFoundException();

        // Устанавливаем сотрудника для расчетного листка.
        ps.setEmployeeId(e.get());

        // Поиск записей о рабочем времени для данного сотрудника и месяца.
        List<TimeSheetModel> t = timeSheetService.searchByYearAndMonth(
                ps.getEmployeeId().getId(),
                ps.getYear(),
                (byte) ps.getMonth()
        );

        // Если записей о рабочем времени нет, выбрасываем исключение.
        if(t.isEmpty()) throw new PaySheetException.PaySheetNotFount();

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

        // Сохранение расчетного листка в базе данных.
        paySheetRepository.save(ps);

        return ps;
    }

    // Метод для получения расчетного листка по его идентификатору.
    public Optional<PaySheetModel> get(long id){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск расчетного листка в базе данных по идентификатору.
        Optional<PaySheetModel> ps = paySheetRepository.findById(id);

        // Если расчетный листок не найден, выбрасываем исключение.
        if(ps.isEmpty()) throw new PaySheetException.PaySheetNotFount();

        return ps;
    }

    // Метод для обновления существующего расчетного листка.
    public PaySheetModel put(long id, PaySheetModel ps){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск существующего расчетного листка в базе данных по идентификатору.
        Optional<PaySheetModel> psDb = paySheetRepository.findById(id);

        // Если расчетный листок не найден, выбрасываем исключение.
        if(psDb.isEmpty()) throw new PaySheetException.PaySheetNotFount();

        // Поиск сотрудника по его идентификатору.
        Optional<EmployeeModel> e = employeeService.get(ps.getEmployeeId().getId());

        // Если сотрудник не найден, выбрасываем исключение.
        if(e.isEmpty()) throw new EmployeeException.EmployeeNotFoundException();

        // Устанавливаем сотрудника для расчетного листка.
        ps.setEmployeeId(e.get());

        // Поиск записей о рабочем времени для данного сотрудника и месяца.
        List<TimeSheetModel> t = timeSheetService.searchByYearAndMonth(
                ps.getEmployeeId().getId(),
                ps.getYear(),
                (byte) ps.getMonth()
        );

        // Если записей о рабочем времени нет, выбрасываем исключение.
        if(t.isEmpty()) throw new PaySheetException.PaySheetNotFount();

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

        // Сохранение обновленного расчетного листка в базе данных.
        paySheetRepository.save(ps);

        return ps;
    }

    // Метод для удаления расчетного листка по его идентификатору.
    public void delete(long id){

        // Проверка, что идентификатор положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск расчетного листка в базе данных по идентификатору.
        Optional<PaySheetModel> ps = paySheetRepository.findById(id);

        // Если расчетный листок не найден, выбрасываем исключение.
        if(ps.isEmpty()) throw new PaySheetException.PaySheetNotFount();

        // Удаление расчетного листка из базы данных.
        paySheetRepository.deleteById(id);
    }

    // Метод для получения списка расчетных листков для конкретного сотрудника.
    public List<PaySheetModel> getByEmployeeId(long id){

        // Проверка, что идентификатор сотрудника положителен.
        if(id <= 0) throw new GeneraleException.InvalidIdException();

        // Поиск расчетных листков для данного сотрудника.
        List<PaySheetModel> ps = paySheetRepository.findAllByEmployeeId_Id(id);

        // Если расчетные листки не найдены, выбрасываем исключение.
        if(ps.isEmpty()) throw new PaySheetException.PaySheetNotFount();

        return ps;
    }
}
