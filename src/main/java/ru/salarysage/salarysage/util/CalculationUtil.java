package ru.salarysage.salarysage.util;

import org.springframework.stereotype.Component;
import ru.salarysage.salarysage.models.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Component
public class CalculationUtil {

    public BigDecimal calculationTotal(List<TimeSheetModel> t, PositionModel p, PaySheetModel pc) {

        // Определите первый день месяца на основе даты в списке
        LocalDate firstDayOfMonth = LocalDate.of(
                t.get(0).getDate().getYear(),
                t.get(0).getDate().getMonth(),
                1
        );

        // Определите последний день месяца, вычитая один день из первого дня следующего месяца
        LocalDate lastDayOfMonth = firstDayOfMonth
                .plusMonths(1)
                .minusDays(1);

        // Рассчитайте количество рабочих дней в месяце (исключая субботу и воскресенье)
        int workerDayMonth = (int) firstDayOfMonth.datesUntil(lastDayOfMonth.plusDays(1))
                .filter(date ->
                        date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                                date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();

        // Рассчитайте общее количество отработанных часов в выходные дни
        int totalHoursWorkedWeekend = t.stream()
                .filter(TimeSheetModel::isHoliday)
                .mapToInt(timeSheet -> timeSheet.getHoursWorked() * 2)
                .sum();

        // Рассчитайте общее количество отработанных часов (не в выходные дни)
        int totalHoursWorked = t.stream()
                .filter(timeSheetModel -> !timeSheetModel.isHoliday())
                .mapToInt(TimeSheetModel::getHoursWorked)
                .sum();

        // Преобразуйте числа в BigDecimal для точных вычислений
        BigDecimal totalHoursWorkedWeekendBD = new BigDecimal(totalHoursWorkedWeekend);
        BigDecimal totalHoursWorkedBD = new BigDecimal(totalHoursWorked);
        BigDecimal workerDayMonthBD = new BigDecimal(workerDayMonth);
        BigDecimal rateBD = p.getRate();

        // Выполните вычисления с точными BigDecimal
        BigDecimal result = ((totalHoursWorkedWeekendBD.add(totalHoursWorkedBD))
                .divide(workerDayMonthBD.multiply(new BigDecimal(8)), MathContext.DECIMAL64)
                .multiply(rateBD))
                .setScale(2, RoundingMode.HALF_UP);

        // Рассчитайте общий процент ставки из списка PaySheetModel
        double totalPercentRate = pc.getRate().stream()
                .mapToDouble(RateModel::getPercent)
                .sum() / 100;

        // Рассчитайте общую сумму выгоды из списка PaySheetModel
        BigDecimal totalSumBenefit = pc.getBenefit().stream()
                .map(BenefitModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Выполните окончательные вычисления и возвратите результат
        return result.subtract(result.subtract(totalSumBenefit)
                        .multiply(BigDecimal.valueOf(totalPercentRate)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
