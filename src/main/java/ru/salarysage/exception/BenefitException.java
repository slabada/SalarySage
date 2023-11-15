package ru.salarysage.exception;

public class BenefitException {
    public static class BenefitAlreadyExistsException extends RuntimeException {
        public BenefitAlreadyExistsException() {
            super("Льгота уже существует");
        }
    }

    public static class NullBenefitException extends RuntimeException {
        public NullBenefitException() {
            super("Льгота не найден");
        }
    }
}
