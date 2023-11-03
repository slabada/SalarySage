package ru.salarysage.salarysage.exception;

public class TimeSheetException {
    public static class DateException extends RuntimeException {
        public DateException() {
            super("Табель с указанной датой существует");
        }
    }

    public static class NullTimeSheetException extends RuntimeException {
        public NullTimeSheetException() {
            super("Табель не существует");
        }
    }

    public static class TimeSheetDataNotFount extends RuntimeException {
        public TimeSheetDataNotFount() {
            super("Табелей на данный месяц нет");
        }
    }

    public static class IllegalDateYearArgumentException extends RuntimeException {
        public IllegalDateYearArgumentException() {
            super("Неверный год");
        }
    }

    public static class IllegalDateMonthArgumentException extends RuntimeException {
        public IllegalDateMonthArgumentException() {
            super("Неверный месяц");
        }
    }
}
