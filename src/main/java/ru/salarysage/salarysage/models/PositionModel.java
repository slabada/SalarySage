package ru.salarysage.salarysage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// Сущность должности

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Position")
public class PositionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "Имя не может быть пустым или состоять из пробелов")
    private String name;
    @Min(value = 0, message = "Ставка должности должна быть больше и не равная 0")
    @NotNull(message = "Ставка не может быть пустой")
    private BigDecimal rate;
}
