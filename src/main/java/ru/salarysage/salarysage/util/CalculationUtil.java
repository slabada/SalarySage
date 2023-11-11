package ru.salarysage.salarysage.util;

import org.springframework.stereotype.Component;
import ru.salarysage.salarysage.models.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

// Расчет ЗП

@Component
public class CalculationUtil {

    private final float standardWorkingHours = 8f;

    private final float multiplier = 2f;

    private final float extraMultiplier = 1.5f;

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
        double totalHoursWorkedWeekend = t.stream()
                .filter(TimeSheetModel::isHoliday)
                .mapToDouble(timeSheet -> {
                    // Извлекаем часы и минуты
                    int hoursWorked = timeSheet.getHoursWorked().toLocalTime().getHour();
                    int minutesWorked = timeSheet.getHoursWorked().toLocalTime().getMinute();

                    // Преобразуем часы в минуты для удобства дальнейших вычислений
                    double totalMinutes = hoursWorked * 60 + minutesWorked;

                    // Умножаем общее количество часов на коэффициент
                    return totalMinutes / 60 * multiplier;
                })
                .sum();


        // Рассчитайте общее количество отработанных часов (не в выходные дни)
        // + сверхурочные (если есть)
        double totalHoursWorked = t.stream()
                .filter(timeSheetModel -> !timeSheetModel.isHoliday())
                .mapToDouble(timeSheet -> {
                    // Извлекаем часы и минуты
                    int hoursWorked = timeSheet.getHoursWorked().toLocalTime().getHour();
                    int minutesWorked = timeSheet.getHoursWorked().toLocalTime().getMinute();

                    // Преобразуем часы в минуты для удобства дальнейших вычислений
                    double totalMinutes = hoursWorked * 60 + minutesWorked;
                    double totalHours = totalMinutes / 60;

                    // Если отработано больше стандартного рабочего дня, считаем сверхурочные
                    if (totalHours > standardWorkingHours) {
                        double overtimeHours = totalHours - standardWorkingHours;
                        // Умножаем сверхурочные на коэффициент
                        return (standardWorkingHours + overtimeHours * extraMultiplier);
                    } else {
                        // Если не превышено, возвращаем обычное количество отработанных часов
                        return totalHours;
                    }
                })
                .sum();

        // Преобразуйте числа в BigDecimal для точных вычислений
        BigDecimal totalHoursWorkedWeekendBD = new BigDecimal(totalHoursWorkedWeekend);
        BigDecimal totalHoursWorkedBD = new BigDecimal(totalHoursWorked);
        BigDecimal workerDayMonthBD = new BigDecimal(workerDayMonth);
        BigDecimal rateBD = p.getRate();

        // Выполните вычисления с точными BigDecimal
        BigDecimal result = ((totalHoursWorkedWeekendBD.add(totalHoursWorkedBD))
                .divide(workerDayMonthBD.multiply(new BigDecimal(standardWorkingHours)), MathContext.DECIMAL64)
                .multiply(rateBD))
                .setScale(2, RoundingMode.HALF_UP);

        // Рассчитайте общий процент налога из списка PaySheetModel
        double totalPercentRate = pc.getRate().stream()
                .mapToDouble(RateModel::getPercent)
                .sum() / 100;

        // Рассчитайте общую сумму льготы из списка PaySheetModel
        BigDecimal totalSumBenefit = pc.getBenefit().stream()
                .map(BenefitModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Выполните окончательные вычисления и возвратите результат
        return result.subtract(result.subtract(totalSumBenefit)
                        .multiply(BigDecimal.valueOf(totalPercentRate)))
                        .setScale(2, RoundingMode.HALF_UP);
    }
}
