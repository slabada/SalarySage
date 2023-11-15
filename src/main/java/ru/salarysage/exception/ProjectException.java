package ru.salarysage.exception;

public class ProjectException {

    public static class ConflictName extends RuntimeException {
        public ConflictName() {
            super("Проект с таким именем уже существует.");
        }
    }

    public static class NoProject extends RuntimeException {
        public NoProject() {
            super("Проект не найден.");
        }
    }
}
