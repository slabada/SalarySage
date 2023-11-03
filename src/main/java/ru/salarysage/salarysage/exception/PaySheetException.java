package ru.salarysage.salarysage.exception;

public class PaySheetException {

    public static class PaySheetNotFount extends RuntimeException {
        public PaySheetNotFount() {
            super("Расчетный лист не найден");
        }
    }
}
