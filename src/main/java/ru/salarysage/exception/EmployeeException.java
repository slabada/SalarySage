package ru.salarysage.exception;

public class EmployeeException {
    public static class EmployeeNotFoundException extends RuntimeException {
        public EmployeeNotFoundException() {
            super("Работник не найден.");
        }
    }

    public static class InvalidPageSizeException extends RuntimeException{
        public InvalidPageSizeException(){
            super("Недопустимые параметры страницы.");
        }
    }
}
