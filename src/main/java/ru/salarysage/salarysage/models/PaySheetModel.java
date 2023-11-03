package ru.salarysage.salarysage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
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

    @NotNull(message = "Дата не может быть пустой")
    private YearMonth date;

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
