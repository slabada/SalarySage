package ru.salarysage.exception;

public class PaySheetException {

    public static class PaySheetNotFount extends RuntimeException {
        public PaySheetNotFount() {
            super("Расчетные листы не найдены");
        }
    }
}
