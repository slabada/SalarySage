package ru.salarysage.salarysage.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.salarysage.salarysage.DTO.ErrorDTO;
import ru.salarysage.salarysage.exception.*;

import java.time.LocalDateTime;
import java.util.Objects;

// Этот класс представляет обработчики исключений.
@RestControllerAdvice
public class Handlers {

    @ExceptionHandler({
            GeneraleException.InvalidIdException.class,
            EmployeeException.InvalidPageSizeException.class,
            TimeSheetException.IllegalDateYearArgumentException.class,
            TimeSheetException.IllegalDateMonthArgumentException.class
    })
    public ResponseEntity<ErrorDTO> handleBadRequest(Exception ex) {
        ErrorDTO error = new ErrorDTO(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({
            PositionException.PositionNotFoundException.class,
            EmployeeException.EmployeeNotFoundException.class,
            TimeSheetException.NullTimeSheetException.class,
            BenefitException.NullBenefitException.class,
            RateException.NullRateException.class,
            PaySheetException.PaySheetNotFount.class,
            TimeSheetException.TimeSheetDataNotFount.class
    })
    public ResponseEntity<?> handleNotFound(Exception ex) {
        ErrorDTO error = new ErrorDTO(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({
            PositionException.PositionAlreadyExistsException.class,
            TimeSheetException.DateException.class,
            BenefitException.BenefitAlreadyExistsException.class,
            RateException.RateAlreadyExistsException.class
    })
    public ResponseEntity<?> handleConflict(Exception ex) {
        ErrorDTO error = new ErrorDTO(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<?> handleConflict(MethodArgumentNotValidException ex) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        ErrorDTO error = new ErrorDTO(
                errorMessage,
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
