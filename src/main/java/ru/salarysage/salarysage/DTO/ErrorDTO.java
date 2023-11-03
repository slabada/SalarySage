package ru.salarysage.salarysage.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// Класс ErrorDTO представляет объект для передачи информации об ошибке.
@AllArgsConstructor
@Getter
public class ErrorDTO {
    private String message;
    private LocalDateTime timestamp;
    private int code;
}
