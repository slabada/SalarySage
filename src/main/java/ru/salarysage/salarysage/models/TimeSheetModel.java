package ru.salarysage.salarysage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;

// Сущность табеля рабочего времени

@Entity
@Getter
@Setter
@Table(name = "Time_Sheet")
public class TimeSheetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Дата не может быть пустой")
    private LocalDate date;

    // Не lazy по той же причине, что и в EmployeeModel

    @NotNull(message = "Сотрудник не может быть пустым")
    @ManyToOne
    private EmployeeModel employeeId;
    private Time hoursWorked;
    private boolean isHoliday;
    private boolean isMedical;
    private String notes;
}
