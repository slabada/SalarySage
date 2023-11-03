package ru.salarysage.salarysage.exception;

public class PositionException {
    public static class PositionNotFoundException extends RuntimeException {
        public PositionNotFoundException() {
            super("Должность не найдена.");
        }
    }

    public static class PositionAlreadyExistsException extends RuntimeException{
        public PositionAlreadyExistsException(){
            super("Такая должность уже существует.");
        }
    }
}
