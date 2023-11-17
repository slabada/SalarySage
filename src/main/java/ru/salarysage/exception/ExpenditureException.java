package ru.salarysage.exception;

public class ExpenditureException {



    public static class ConflictName extends RuntimeException {
        public ConflictName() {
            super("Доп.расход с таким именем уже существует.");
        }
    }

    public static class NoExpenditure extends RuntimeException {
        public NoExpenditure() {
            super("Доп.расход не найден");
        }
    }
}
