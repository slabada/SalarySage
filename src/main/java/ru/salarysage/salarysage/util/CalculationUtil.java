package ru.salarysage.salarysage.util;

import org.springframework.stereotype.Component;
import ru.salarysage.salarysage.models.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public class CalculationUtil {

    // Обычное время работы
    private static final float STANDARD_WORKING_HOURS = 8f;
    // Ставка при выходе выходное
    private static final float WEEKEND_MULTIPLIER = 2f;
    // Ставка в переработках
    private static final float OVERTIME_MULTIPLIER = 1.5f;
    // Процент больничного (от 0 до 1)
    private static final float MEDICAL_PERCENT = 0.7f;

    // Метод для расчета общей суммы
    public BigDecimal calculationTotal(List<TimeSheetModel> timeSheetList, PositionModel position, PaySheetModel paySheet) {
        // Получение первого дня месяца
        LocalDate firstDayOfMonth = getFirstDayOfMonth(timeSheetList);
        // Получение последнего дня месяца
        LocalDate lastDayOfMonth = getLastDayOfMonth(firstDayOfMonth);

        // Расчет рабочих дней в месяце
        int workerDayMonth = calculateWorkerDays(firstDayOfMonth, lastDayOfMonth);

        // Расчет общего числа больничных дней
        int totalMedicalDay = calculateMedicalDays(timeSheetList);

        // Расчет дневной зарплаты
        BigDecimal salaryDay = calculateSalaryDay(position.getRate(), workerDayMonth);

        // Расчет вычета за больничные
        BigDecimal medicalDeduction = calculateMedicalDeduction(salaryDay, totalMedicalDay);

        // Расчет общего числа отработанных часов в выходные
        double totalHoursWorkedWeekend = calculateTotalHoursWorkedWeekend(timeSheetList);

        // Расчет общего числа отработанных часов
        double totalHoursWorked = calculateTotalHoursWorked(timeSheetList);

        // Расчет итоговой суммы
        BigDecimal result = calculateResult(totalHoursWorkedWeekend, totalHoursWorked, workerDayMonth, position.getRate());

        // Расчет общего процента ставки
        double totalPercentRate = calculateTotalPercentRate(paySheet.getRate());

        // Расчет общей суммы льгот
        BigDecimal totalSumBenefit = calculateTotalSumBenefit(paySheet.getBenefit());

        // Расчет конечного результата
        BigDecimal totalResult = calculateFinalResult(result, totalSumBenefit, totalPercentRate);

        // Возвращение итоговой суммы с вычетом больничного
        return totalResult.subtract(medicalDeduction).setScale(2, RoundingMode.HALF_UP);
    }

    // Получение первого дня месяца из временного листа
    private LocalDate getFirstDayOfMonth(List<TimeSheetModel> timeSheetList) {
        return LocalDate.of(timeSheetList.get(0).getDate().getYear(),
                timeSheetList.get(0).getDate().getMonth(),
                1);
    }

    // Получение последнего дня месяца от первого дня месяца
    private LocalDate getLastDayOfMonth(LocalDate firstDayOfMonth) {
        return firstDayOfMonth.plusMonths(1).minusDays(1);
    }

    // Расчет рабочих дней между двумя датами
    private int calculateWorkerDays(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth) {
        return (int) firstDayOfMonth.datesUntil(lastDayOfMonth.plusDays(1))
                .filter(date ->
                                date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                                date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
    }

    // Расчет общего числа больничных дней
    private int calculateMedicalDays(List<TimeSheetModel> timeSheetList) {
        return (int) timeSheetList.stream()
                .filter(TimeSheetModel::isMedical)
                .count();
    }

    // Расчет дневной зарплаты
    private BigDecimal calculateSalaryDay(BigDecimal rate, int workerDayMonth) {
        return rate.divide(BigDecimal.valueOf(workerDayMonth), 2, RoundingMode.HALF_UP);
    }

    // Расчет вычета за больничные
    private BigDecimal calculateMedicalDeduction(BigDecimal salaryDay, int totalMedicalDay) {
        return salaryDay.multiply(BigDecimal.valueOf(1 - MEDICAL_PERCENT))
                .multiply(BigDecimal.valueOf(totalMedicalDay));
    }

    // Расчет общего числа отработанных часов в выходные
    private double calculateTotalHoursWorkedWeekend(List<TimeSheetModel> timeSheetList) {
        return timeSheetList.stream()
                .filter(TimeSheetModel::isHoliday)
                .mapToDouble(this::calculateWeekendHours)
                .sum();
    }

    // Расчет часов отработки в выходные
    private double calculateWeekendHours(TimeSheetModel timeSheet) {
        int hoursWorked = timeSheet.getHoursWorked().toLocalTime().getHour();
        int minutesWorked = timeSheet.getHoursWorked().toLocalTime().getMinute();
        double totalMinutes = hoursWorked * 60 + minutesWorked;
        return totalMinutes / 60 * WEEKEND_MULTIPLIER;
    }

    // Расчет общего числа отработанных часов
    private double calculateTotalHoursWorked(List<TimeSheetModel> timeSheetList) {
        return timeSheetList.stream()
                .filter(timeSheetModel -> !timeSheetModel.isHoliday())
                .mapToDouble(this::calculateRegularAndOvertimeHours)
                .sum();
    }

    // Расчет часов отработки (обычных и в переработках)
    private double calculateRegularAndOvertimeHours(TimeSheetModel timeSheet) {
        int hoursWorked = timeSheet.getHoursWorked().toLocalTime().getHour();
        int minutesWorked = timeSheet.getHoursWorked().toLocalTime().getMinute();
        double totalMinutes = hoursWorked * 60 + minutesWorked;
        double totalHours = totalMinutes / 60;

        if (totalHours > STANDARD_WORKING_HOURS) {
            double overtimeHours = totalHours - STANDARD_WORKING_HOURS;
            return (STANDARD_WORKING_HOURS + overtimeHours * OVERTIME_MULTIPLIER);
        } else {
            return totalHours;
        }
    }

    // Расчет итоговой суммы
    private BigDecimal calculateResult(double totalHoursWorkedWeekend, double totalHoursWorked, int workerDayMonth, BigDecimal rate) {
        BigDecimal totalHoursWorkedWeekendBD = new BigDecimal(totalHoursWorkedWeekend);
        BigDecimal totalHoursWorkedBD = new BigDecimal(totalHoursWorked);
        BigDecimal workerDayMonthBD = new BigDecimal(workerDayMonth);

        return ((totalHoursWorkedWeekendBD.add(totalHoursWorkedBD))
                .divide(workerDayMonthBD.multiply(new BigDecimal(STANDARD_WORKING_HOURS)), MathContext.DECIMAL64)
                .multiply(rate))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Расчет общего процента ставки
    private double calculateTotalPercentRate(Set<RateModel> rateList) {
        return rateList.stream()
                .mapToDouble(RateModel::getPercent)
                .sum() / 100;
    }

    // Расчет общей суммы льгот
    private BigDecimal calculateTotalSumBenefit(Set<BenefitModel> benefitList) {
        return benefitList.stream()
                .map(BenefitModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Расчет конечного результата
    private BigDecimal calculateFinalResult(BigDecimal result, BigDecimal totalSumBenefit, double totalPercentRate) {
        return result.subtract(result.subtract(totalSumBenefit).multiply(BigDecimal.valueOf(totalPercentRate)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
