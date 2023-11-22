package ru.salarysage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// Сущность доп расхода

@Entity
@Getter
@Setter
@Table(name = "expenditure")
public class ExpenditureModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Название не может быть пустым или состоять из пробелов")
    private String name;

    @Min(value = 0, message = "Сумма не может быть меньше 0")
    private BigDecimal amount;
}
