package ru.salarysage.salarysage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

// Сущность расчетного листа
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Pay_Sheet")
public class PaySheetModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @NotNull(message = "Дата не может быть пустой")
//    @DateTimeFormat(pattern = "HHHH-mm")
//    private LocalDate date;

    @Min(value = 1970, message = "Год не может быть меньше 1970")
    @Max(value = 2100, message = "Год не может быть больше 2100")
    private int year;

    @Min(value = 1, message = "Месяц не может быть меньше 1")
    @Max(value = 2100, message = "Месяц не может быть больше 12")
    private int month;

    @NotNull(message = "сотрудник не может быть пустой")
    @ManyToOne
    private EmployeeModel employeeId;

    // Не lazy по той же причине, что и в EmployeeModel

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "Pay_Sheet"),
            inverseJoinColumns = @JoinColumn(name = "Benefit_Id"),
            name = "Pay_Sheet_Benefit"
    )
    private Set<BenefitModel> benefit;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "Pay_Sheet"),
            inverseJoinColumns = @JoinColumn(name = "Rate_Id"),
            name = "Pay_Sheet_Rate"
    )
    private Set<RateModel> rate;

    private BigDecimal totalAmount;
}
