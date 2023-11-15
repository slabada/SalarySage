package ru.salarysage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// сущность льготы
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Benefit")
public class BenefitModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Имя не может быть пустым или состоять из пробелов")
    private String name;
    @NotNull(message = "Сумма не может быть пустой")
    @Min(value = 0, message = "Сумма не может быть меньше или равная 0")
    private BigDecimal amount;
}
