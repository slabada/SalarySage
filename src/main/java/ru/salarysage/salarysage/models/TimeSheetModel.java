package ru.salarysage.salarysage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// Сущность табеля рабочего времени

@Entity
@Getter
@Setter
public class TimeSheetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "Дата не может быть пустой")
    private LocalDate date;
    @NotNull(message = "Сотрудник не может быть пустым")

    // Не lazy по той же причине, что и в EmployeeModel

    @ManyToOne
    private EmployeeModel employeeId;
    @Min(value = 0, message = "отработанные часы не могут быть меньше или равные 0")
    private int hoursWorked;
    private boolean isHoliday;
    private String notes;
}
