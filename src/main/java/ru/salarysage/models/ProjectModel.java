package ru.salarysage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Project")
public class ProjectModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Название не может быть пустым или состоять из пробелов")
    private String name;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "Project"),
            inverseJoinColumns = @JoinColumn(name = "Employee"),
            name = "Project_Employee"
    )
    private Set<EmployeeModel> employees;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "Project"),
            inverseJoinColumns = @JoinColumn(name = "Expenditure"),
            name = "Project_Expenditure"
    )
    private Set<ExpenditureModel> expenditure;
}
