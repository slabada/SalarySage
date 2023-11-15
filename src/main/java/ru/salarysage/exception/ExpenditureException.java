package ru.salarysage.exception;

public class ExpenditureException {



    public static class ConflictName extends RuntimeException {
        public ConflictName() {
            super("Расход с таким именем уже существует.");
        }
    }

    public static class NoExpenditure extends RuntimeException {
        public NoExpenditure() {
            super("Расход не найден");
        }
    }
}
