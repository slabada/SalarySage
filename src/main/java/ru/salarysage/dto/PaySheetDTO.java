package ru.salarysage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.salarysage.models.BenefitModel;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.RateModel;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaySheetDTO {
    private int year;
    private int month;
    private EmployeeDTO employeeId;
    private Set<BenefitDTO> benefit;
    private Set<RateDTO> rate;
    private BigDecimal totalAmount;
}
