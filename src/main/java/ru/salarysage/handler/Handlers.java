package ru.salarysage.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.salarysage.dto.ErrorDTO;
import ru.salarysage.exception.*;

import java.time.LocalDateTime;
import java.util.Objects;

// Этот класс представляет обработчики исключений.
@RestControllerAdvice
public class Handlers {

    @ExceptionHandler({
            GeneraleException.InvalidIdException.class,
            EmployeeException.InvalidPageSizeException.class,
            TimeSheetException.IllegalDateYearArgumentException.class,
            TimeSheetException.IllegalDateMonthArgumentException.class,
            ProjectException.ConflictName.class
    })
    public ResponseEntity<ErrorDTO> handleBadRequest(Exception ex) {
        ErrorDTO error = new ErrorDTO(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(error.getCode()).body(error);
    }

    @ExceptionHandler({
            PositionException.PositionNotFoundException.class,
            EmployeeException.EmployeeNotFoundException.class,
            TimeSheetException.NullTimeSheetException.class,
            BenefitException.NullBenefitException.class,
            RateException.NullRateException.class,
            PaySheetException.PaySheetNotFount.class,
            TimeSheetException.TimeSheetDataNotFount.class,
            TimeSheetException.NoDatesException.class,
            ExpenditureException.NoExpenditure.class,
            ProjectException.NoProject.class
    })
    public ResponseEntity<ErrorDTO> handleNotFound(Exception ex) {
        ErrorDTO error = new ErrorDTO(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(error.getCode()).body(error);
    }

    @ExceptionHandler({
            PositionException.PositionAlreadyExistsException.class,
            TimeSheetException.DateException.class,
            BenefitException.BenefitAlreadyExistsException.class,
            RateException.RateAlreadyExistsException.class,
            ExpenditureException.ConflictName.class
    })
    public ResponseEntity<ErrorDTO> handleConflict(Exception ex) {
        ErrorDTO error = new ErrorDTO(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(error.getCode()).body(error);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorDTO> handleConflict(MethodArgumentNotValidException ex) {

        String errorMessage = Objects.requireNonNull(ex.getBindingResult()
                .getFieldError()).getDefaultMessage();

        ErrorDTO error = new ErrorDTO(
                errorMessage,
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(error.getCode()).body(error);
    }
}
