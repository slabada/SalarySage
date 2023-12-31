package ru.salarysage.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import ru.salarysage.dto.*;
import ru.salarysage.exception.*;
import ru.salarysage.util.CalculationAnalyticsUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentsService {

    protected final ProjectService projectService;
    protected final CalculationAnalyticsUtil calculationAnalyticsUtil;
    protected final TimeSheetService timeSheetService;
    protected final EmployeeService employeeService;
    protected final PaySheetService paySheetService;

    // Тип контента и заголовки для Word документа
    private static final String CONTENT_TYPE = "application/word";
    private static final String CONTENT_DISPOSITION = "attachment; filename=document.doc";
    // Тип контента и заголовки для Excel документа
    private static final String EXCEL_CONTENT_TYPE = "application/exel";
    private static final String EXCEL_CONTENT_DISPOSITION = "attachment; filename=document.xlsx";

    public byte[] generateWordProjectDocument(long id, HttpServletResponse response) throws IOException {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        ProjectDTO p = projectService.get(id).orElseThrow(ProjectException.NoProject::new);
        // Создание объекта XWPFDocument, представляющего Word документ
        XWPFDocument document = new XWPFDocument();
        // Генерация содержимого документа: информация о проекте, участниках разработки, дополнительные расходы
        createProjectInfo(document, p, id);
        createDevelopersTable(document, p);
        createExpenditureTable(document, p);
        // Преобразование документа в массив байт
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.write(byteArrayOutputStream);
        byte[] documentBytes = byteArrayOutputStream.toByteArray();
        // Настройка HTTP-ответа: тип контента и заголовки для скачивания
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Content-Disposition", CONTENT_DISPOSITION);
        // Запись массива байт в выходной поток HTTP-ответа
        OutputStream out = response.getOutputStream();
        out.write(documentBytes);
        out.close();
        return documentBytes;
    }
    // Вспомогательный метод для установки стиля текста в объекте XWPFRun
    private void setRunStyle(XWPFRun run) {
        run.setFontSize(14);
        run.setFontFamily("Times New Roman");
        run.addBreak();
    }
    // Вспомогательный метод для создания информации о проекте в Word документе
    private void createProjectInfo(XWPFDocument document, ProjectDTO project, long id) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        run.setText("Проект: " + project.getName());
        setRunStyle(run);

        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("Сроки: " + project.getStartDate() + " - " + project.getEndDate());
        setRunStyle(run);

        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("Бюджет проекта: " + calculationAnalyticsUtil.calculationTotal(id));
        setRunStyle(run);
    }
    // Вспомогательный метод для создания таблицы участников разработки в Word документе
    private void createDevelopersTable(XWPFDocument document, ProjectDTO project) {
        // Создание параграфа и объекта XWPFRun для заголовка таблицы
        XWPFParagraph developersParagraph = document.createParagraph();
        XWPFRun developersRun = developersParagraph.createRun();
        developersRun.addBreak();
        developersRun.setText("Участники разработки");
        developersRun.setFontSize(16);
        developersRun.setBold(true);
        // Создание таблицы и установка ширины
        XWPFTable developersTable = document.createTable(1, 3);
        developersTable.setWidth("100%");
        // Получение строки заголовка таблицы и установка названий столбцов
        XWPFTableRow headerRowEmployee = developersTable.getRow(0);
        headerRowEmployee.getCell(0).setText("ФИО");
        headerRowEmployee.getCell(1).setText("Должность");
        headerRowEmployee.getCell(2).setText("Оклад");
        // Получение списка участников разработки и заполнение данных в таблицу
        Set<EmployeeDTO> employees = project.getEmployees();
        for (EmployeeDTO employee : employees) {
            if (employee != null) {
                XWPFTableRow dataRow = developersTable.createRow();
                dataRow.getCell(0).setText(employee.getLastName() + " " + employee.getFirstName());
                dataRow.getCell(1).setText(employee.getPosition().getName());
                dataRow.getCell(2).setText(employee.getPosition().getRate().toString());
            }
        }
    }
    // Вспомогательный метод для создания таблицы дополнительных расходов в Word документе
    private void createExpenditureTable(XWPFDocument document, ProjectDTO project) {
        // Создание параграфа и объекта XWPFRun для заголовка таблицы
        XWPFParagraph expenditureParagraph = document.createParagraph();
        XWPFRun expenditureRun = expenditureParagraph.createRun();
        expenditureRun.addBreak();

        expenditureRun.setText("Доп.расходники");
        expenditureRun.setFontSize(16);
        expenditureRun.setBold(true);
        // Создание таблицы и установка ширины
        XWPFTable expenditureTable = document.createTable(1, 2);
        expenditureTable.setWidth("100%");
        // Получение строки заголовка таблицы и установка названий столбцов
        XWPFTableRow headerRowExpenditure = expenditureTable.getRow(0);
        headerRowExpenditure.getCell(0).setText("Название");
        headerRowExpenditure.getCell(1).setText("Сумма");
        // Получение списка дополнительных расходов и заполнение данных в таблицу
        Set<ExpenditureDTO> expenditures = project.getExpenditure();
        for (ExpenditureDTO expenditure : expenditures) {
            XWPFTableRow dataRow = expenditureTable.createRow();
            dataRow.getCell(0).setText(expenditure.getName());
            dataRow.getCell(1).setText(expenditure.getAmount().toString());
        }
    }
    // Метод для генерации Excel документа на основе информации о проекте
    public byte[] generateExcelProjectDocument(long id, HttpServletResponse response) throws IOException {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        ProjectDTO p = projectService.get(id)
                .orElseThrow(ProjectException.NoProject::new);
        // Создание объекта Workbook, представляющего Excel документ
        Workbook workbook = new XSSFWorkbook();
        // Генерация содержимого документа: информация о проекте, участниках разработки, дополнительные расходы
        createExcelProjectInfo(id, workbook, p);
        createExcelDevelopersSheet(workbook, p);
        createExcelExpenditureSheet(workbook, p);
        // Преобразование документа в массив байт
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        byte[] documentBytes = byteArrayOutputStream.toByteArray();
        // Настройка HTTP-ответа: тип контента и заголовки для скачивания
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader("Content-Disposition", EXCEL_CONTENT_DISPOSITION);
        // Запись массива байт в выходной поток HTTP-ответа
        OutputStream out = response.getOutputStream();
        out.write(documentBytes);
        out.close();
        return documentBytes;
    }
    // Вспомогательный метод для создания информации о проекте в Excel документе
    private void createExcelProjectInfo(long id,Workbook workbook, ProjectDTO project) {
        // Создание листа с названием "Общая информация"
        Sheet sheet = workbook.createSheet("Общая информация");
        // Создание строки заголовка
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Проект");
        headerRow.createCell(1).setCellValue("Сроки");
        headerRow.createCell(2).setCellValue("Бюджет");
        // Создание строки данных
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(project.getName());
        dataRow.createCell(1).setCellValue(project.getStartDate() + " - " + project.getEndDate());
        dataRow.createCell(2).setCellValue(calculationAnalyticsUtil.calculationTotal(id).doubleValue());
    }
    // Вспомогательный метод для создания листа с информацией о разработчиках в Excel документе
    private void createExcelDevelopersSheet(Workbook workbook, ProjectDTO project) {
        // Создание листа с названием "Разработчики"
        Sheet sheet = workbook.createSheet("Разработчики");
        // Создание строки заголовка
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ФИО");
        headerRow.createCell(1).setCellValue("Должность");
        headerRow.createCell(2).setCellValue("Оклад");
        // Получение списка участников разработки и заполнение данных в таблицу
        Set<EmployeeDTO> employees = project.getEmployees();
        int rowNum = 1;
        for (EmployeeDTO employee : employees) {
            if (employee != null) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(employee.getLastName() + " " + employee.getFirstName());
                dataRow.createCell(1).setCellValue(employee.getPosition().getName());
                dataRow.createCell(2).setCellValue(employee.getPosition().getRate().doubleValue());
            }
        }
    }
    // Вспомогательный метод для создания листа с информацией о дополнительных расходах в Excel документе
    private void createExcelExpenditureSheet(Workbook workbook, ProjectDTO project) {
        // Создание листа с названием "Доп.расходы"
        Sheet sheet = workbook.createSheet("Доп.расходы");
        // Создание строки заголовка
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Название");
        headerRow.createCell(1).setCellValue("Сумма");
        // Получение списка дополнительных расходов и заполнение данных в таблицу
        Set<ExpenditureDTO> expenditures = project.getExpenditure();
        int rowNum = 1;
        for (ExpenditureDTO expenditure : expenditures) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(expenditure.getName());
            dataRow.createCell(1).setCellValue(expenditure.getAmount().doubleValue());
        }
    }
    // Метод для генерации Word документа на основе информации табели времени
    public byte[] generateWordTimeSheetDocument(long id, HttpServletResponse response) throws IOException {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        List<TimeSheetDTO> t = timeSheetService.getAll(id);
        if (t.isEmpty()){
            throw new TimeSheetException.NullTimeSheetException();
        }
        EmployeeDTO e = employeeService.get(id)
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        // Создание объекта XWPFDocument, представляющего Word документ
        XWPFDocument document = new XWPFDocument();
        // Генерация содержимого документа: информация о проекте, участниках разработки, дополнительные расходы
        createTimeSheetTable(document, t, e);
        // Преобразование документа в массив байт
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.write(byteArrayOutputStream);
        byte[] documentBytes = byteArrayOutputStream.toByteArray();
        // Настройка HTTP-ответа: тип контента и заголовки для скачивания
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Content-Disposition", CONTENT_DISPOSITION);
        // Запись массива байт в выходной поток HTTP-ответа
        OutputStream out = response.getOutputStream();
        out.write(documentBytes);
        out.close();
        return documentBytes;
    }
    // Вспомогательный метод для создания таблицы табеля времени word
    private void createTimeSheetTable(XWPFDocument document, List<TimeSheetDTO> t, EmployeeDTO e) {
        // Создание параграфа и объекта XWPFRun для заголовка таблицы
        XWPFParagraph timeSheetParagraph = document.createParagraph();
        XWPFRun timeSheetRun = timeSheetParagraph.createRun();

        timeSheetRun.setText("Табели сотрудника - " + e.getFirstName() + " " + e.getLastName());
        timeSheetRun.setFontSize(16);
        timeSheetRun.setBold(true);
        // Создание таблицы и установка ширины
        XWPFTable timeSheetTable = document.createTable(1, 6);
        timeSheetTable.setWidth("100%");
        // Получение строки заголовка таблицы и установка названий столбцов
        XWPFTableRow headerRowTimeSheet = timeSheetTable.getRow(0);

        headerRowTimeSheet.getCell(0).setText("Дата");
        headerRowTimeSheet.getCell(1).setText("Часы");
        headerRowTimeSheet.getCell(2).setText("Выходной");
        headerRowTimeSheet.getCell(3).setText("Больничный");
        headerRowTimeSheet.getCell(4).setText("Отпуск");
        headerRowTimeSheet.getCell(5).setText("Примечание");

        for (TimeSheetDTO times : t){
            XWPFTableRow dataRow = timeSheetTable.createRow();
            dataRow.getCell(0).setText(times.getDate().toString());
            dataRow.getCell(1).setText(times.getHoursWorked().toString());
            dataRow.getCell(2).setText(isBool(times.isHoliday()));
            dataRow.getCell(3).setText(isBool(times.isMedical()));
            dataRow.getCell(4).setText(isBool(times.isVacation()));
            dataRow.getCell(5).setText(times.getNotes());
        }
    }
    // Метод для генерации Excel документа на основе информации табели времени
    public byte[] generateExcelTimeSheetDocument(long id, HttpServletResponse response) throws IOException {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        List<TimeSheetDTO> t = timeSheetService.getAll(id);
        if (t.isEmpty()){
            throw new TimeSheetException.NullTimeSheetException();
        }
        EmployeeDTO e = employeeService.get(id)
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        // Создание нового Excel документа
        Workbook workbook = new XSSFWorkbook();
        // Генерация содержимого документа: информация о проекте, участниках разработки, дополнительные расходы
        createTimeSheetSheet(workbook, t, e);
        // Преобразование документа в массив байт
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        byte[] documentBytes = byteArrayOutputStream.toByteArray();
        // Настройка HTTP-ответа: тип контента и заголовки для скачивания
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader("Content-Disposition", EXCEL_CONTENT_DISPOSITION);
        // Запись массива байт в выходной поток HTTP-ответа
        OutputStream out = response.getOutputStream();
        out.write(documentBytes);
        out.close();
        return documentBytes;
    }
    // Вспомогательный метод для создания таблицы табеля времени excel
    private void createTimeSheetSheet(Workbook workbook, List<TimeSheetDTO> t, EmployeeDTO e) {
        Sheet sheet = workbook.createSheet(e.getFirstName() + ' ' + e.getLastName());
        // Создание строки заголовка
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Дата");
        headerRow.createCell(1).setCellValue("Отработанные часы");
        headerRow.createCell(2).setCellValue("Выходной");
        headerRow.createCell(3).setCellValue("Больничный");
        headerRow.createCell(4).setCellValue("Отпуск");
        headerRow.createCell(5).setCellValue("Примечание");
        // Заполнение данными
        for (int i = 0; i < t.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            TimeSheetDTO times = t.get(i);

            dataRow.createCell(0).setCellValue(times.getDate().toString());
            dataRow.createCell(1).setCellValue(times.getHoursWorked().toString());
            dataRow.createCell(2).setCellValue(isBool(times.isHoliday()));
            dataRow.createCell(3).setCellValue(isBool(times.isMedical()));
            dataRow.createCell(4).setCellValue(isBool(times.isVacation()));
            dataRow.createCell(5).setCellValue(times.getNotes());
        }
    }
    // Метод для генерации Word документа на основе информации финансовый отчет
    public byte[] generateWordPaySheetDocument(long id, HttpServletResponse response) throws IOException {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        List<PaySheetDTO> p = paySheetService.getAll(id);
        if (p.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        EmployeeDTO e = employeeService.get(id)
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        // Создание объекта XWPFDocument, представляющего Word документ
        XWPFDocument document = new XWPFDocument();
        // Генерация содержимого документа: информация о проекте, участниках разработки, дополнительные расходы
        createPaySheetTable(document, p, e);
        // Преобразование документа в массив байт
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.write(byteArrayOutputStream);
        byte[] documentBytes = byteArrayOutputStream.toByteArray();
        // Настройка HTTP-ответа: тип контента и заголовки для скачивания
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Content-Disposition", CONTENT_DISPOSITION);
        // Запись массива байт в выходной поток HTTP-ответа
        OutputStream out = response.getOutputStream();
        out.write(documentBytes);
        out.close();
        return documentBytes;
    }
    // Вспомогательный метод для создания таблицы табеля времени word
    private void createPaySheetTable(XWPFDocument document, List<PaySheetDTO> p, EmployeeDTO e) {
        // Создание параграфа и объекта XWPFRun для заголовка таблицы
        XWPFParagraph paySheetParagraph = document.createParagraph();
        XWPFRun paySheetRun = paySheetParagraph.createRun();

        paySheetRun.setText("Финансовый отчет - " + e.getFirstName() + " " + e.getLastName());
        paySheetRun.setFontSize(16);
        paySheetRun.setBold(true);
        // Создание таблицы и установка ширины
        XWPFTable paySheetTable = document.createTable(1, 6);
        paySheetTable.setWidth("100%");
        // Получение строки заголовка таблицы и установка названий столбцов
        XWPFTableRow headerRowPaySheet = paySheetTable.getRow(0);

        headerRowPaySheet.getCell(0).setText("Дата выплаты");
        headerRowPaySheet.getCell(1).setText("Должность");
        headerRowPaySheet.getCell(2).setText("Оклад");
        headerRowPaySheet.getCell(3).setText("Льготы");
        headerRowPaySheet.getCell(4).setText("Налоги");
        headerRowPaySheet.getCell(5).setText("Итоговая сумма");

        for (PaySheetDTO paySheets : p){
            XWPFTableRow dataRow = paySheetTable.createRow();
            dataRow.getCell(0).setText(paySheets.getYear() + " - " + paySheets.getMonth());
            dataRow.getCell(1).setText(paySheets.getEmployeeId().getPosition().getName());
            dataRow.getCell(2).setText(paySheets.getEmployeeId().getPosition().getRate().toString());
            dataRow.getCell(3).setText(
                    paySheets.getBenefit()
                            .stream()
                            .map(BenefitDTO::getName)
                            .collect(Collectors.joining(", "))
            );
            dataRow.getCell(4).setText(
                    paySheets.getRate()
                            .stream()
                            .map(RateDTO::getName)
                            .collect(Collectors.joining(", "))
            );
            dataRow.getCell(5).setText(paySheets.getTotalAmount().toString());
        }
    }
    // Метод для генерации Excel документа на основе информации финансовый отчет
    public byte[] generateExcelPaySheetDocument(long id, HttpServletResponse response) throws IOException {
        if (id <= 0){
            throw new GeneraleException.InvalidIdException();
        }
        // Получение проекта по идентификатору
        List<PaySheetDTO> p = paySheetService.getAll(id);
        // Обработка случая, если проект не найден
        if (p.isEmpty()){
            throw new PaySheetException.PaySheetNotFount();
        }
        EmployeeDTO e = employeeService.get(id)
                .orElseThrow(EmployeeException.EmployeeNotFoundException::new);
        // Создание нового Excel документа
        Workbook workbook = new XSSFWorkbook();
        // Генерация содержимого документа: информация о проекте, участниках разработки, дополнительные расходы
        createExcelPaySheet(workbook, p, e);
        // Преобразование документа в массив байт
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        byte[] documentBytes = byteArrayOutputStream.toByteArray();
        // Настройка HTTP-ответа: тип контента и заголовки для скачивания
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader("Content-Disposition", EXCEL_CONTENT_DISPOSITION);
        // Запись массива байт в выходной поток HTTP-ответа
        OutputStream out = response.getOutputStream();
        out.write(documentBytes);
        out.close();
        return documentBytes;
    }
    // Вспомогательный метод для создания таблицы табеля времени excel
    private static void createExcelPaySheet(Workbook workbook, List<PaySheetDTO> paySheets, EmployeeDTO employee) {
        Sheet sheet = workbook.createSheet(employee.getFirstName() + " " + employee.getLastName());
        // Создание строки заголовка
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Дата выплаты");
        headerRow.createCell(1).setCellValue("Должность");
        headerRow.createCell(2).setCellValue("Оклад");
        headerRow.createCell(3).setCellValue("Льготы");
        headerRow.createCell(4).setCellValue("Налоги");
        headerRow.createCell(5).setCellValue("Итоговая сумма");
        // Заполнение данными
        for (int i = 0; i < paySheets.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            PaySheetDTO paySheet = paySheets.get(i);

            dataRow.createCell(0).setCellValue(paySheet.getYear() + " - " + paySheet.getMonth());
            dataRow.createCell(1).setCellValue(paySheet.getEmployeeId().getPosition().getName());
            dataRow.createCell(2).setCellValue(paySheet.getEmployeeId().getPosition().getRate().toString());
            dataRow.createCell(3).setCellValue(
                    paySheet.getBenefit()
                            .stream()
                            .map(BenefitDTO::getName)
                            .collect(Collectors.joining(", "))
            );
            dataRow.createCell(4).setCellValue(
                    paySheet.getRate()
                            .stream()
                            .map(RateDTO::getName)
                            .collect(Collectors.joining(", "))
            );
            dataRow.createCell(5).setCellValue(paySheet.getTotalAmount().toString());
        }
    }
    // Подстановка ДА НЕТ в таблицу вместо true и false
    private String isBool(boolean b){
        return b ? "Да" : "Нет";
    }
}
