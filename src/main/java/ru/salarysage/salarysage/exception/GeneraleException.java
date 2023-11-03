package ru.salarysage.salarysage.exception;

public class GeneraleException {
    public static class InvalidIdException extends RuntimeException {
        public InvalidIdException() {super("Предоставлен неверный идентификатор.");}
    }
}
