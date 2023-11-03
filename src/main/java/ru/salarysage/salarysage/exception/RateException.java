package ru.salarysage.salarysage.exception;

public class RateException {
    public static class RateAlreadyExistsException extends RuntimeException {
        public RateAlreadyExistsException() {
            super("Налог уже существует");
        }
    }
    public static class NullRateException extends RuntimeException {
        public NullRateException() {
            super("Налог не найден");
        }
    }
}
