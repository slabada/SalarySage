package ru.salarysage.util;

import org.springframework.stereotype.Component;
import ru.salarysage.controllers.ProjectController;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.models.ProjectModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class CalculationAnalyticsUtil {

    protected final ProjectController projectController;

    public CalculationAnalyticsUtil(ProjectController projectController) {
        this.projectController = projectController;
    }

    // Считаем общую сумму
    public BigDecimal calculationTotal(long id){

        // Ищем проект по id
        Optional<ProjectModel> p = projectController.get(id);

        // Получение первый день проекта
        LocalDate firstDayProject = getFirstDayOfMonth(p.get().getStartDate());

        // Получение последнего день проекта
        LocalDate lastDayProject = getLastDayOfMonth(firstDayProject);

        // Считаем общий бюджет сотрудников
        BigDecimal budget = calculationBudgetEmployee(p);

        // Считаем общий бюджет расходов
        BigDecimal expenditure = calculationBudgetExpenditure(p);

        // Считаем количество рабочих дней в текущем месяце отведенные на проект
        int workerDays = calculateWorkerDays(firstDayProject, lastDayProject);

        BigDecimal budgetDay = calculationBudgetDay(budget, workerDays);

        int workerDaysProject = calculateWorkerDaysProject(p.get().getStartDate(), p.get().getEndDate());

        return budgetDay.multiply(BigDecimal.valueOf(workerDaysProject)).add(expenditure);
    }

    // Считаем бюджет на сотрудников
    public BigDecimal calculationBudgetEmployee(Optional<ProjectModel> p){
        return p.get().getEmployees().stream()
                .map(EmployeeModel -> EmployeeModel.getPosition().getRate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Получение первого дня месяца из временного листа
    private LocalDate getFirstDayOfMonth(LocalDate date) {
        return LocalDate.of(
                date.getYear(),
                date.getMonth(),
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

    // Находим дневной бюджет
    public BigDecimal calculationBudgetDay(BigDecimal budgetEmployee, int workerDays){
        return budgetEmployee.divide(BigDecimal.valueOf(workerDays), 2, RoundingMode.HALF_UP);
    }

    // Считаем количество рабочих дней отведенные на проект
    public int calculateWorkerDaysProject(LocalDate start, LocalDate end){
        return (int) start.datesUntil(end.plusDays(1))
                .filter(date ->
                                date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                                date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
    }

    // Считаем бюджет на доп расходы
    public BigDecimal calculationBudgetExpenditure(Optional<ProjectModel> p){
        return p.get().getExpenditure().stream()
                .map(ExpenditureModel::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}