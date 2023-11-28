package ru.salarysage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSheetDTO {
    private LocalDate date;
    private EmployeeDTO employeeId;
    private Time hoursWorked;
    private boolean isHoliday;
    private boolean isMedical;
    private boolean isVacation;
    private String notes;
}
