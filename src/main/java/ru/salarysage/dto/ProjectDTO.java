package ru.salarysage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<EmployeeDTO> employees;
    private Set<ExpenditureDTO> expenditure;
}
