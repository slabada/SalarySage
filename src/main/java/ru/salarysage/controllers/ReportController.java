package ru.salarysage.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.salarysage.service.DocumentsService;

import java.io.IOException;

@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    protected final PaySheetController paySheetController;
    protected final EmployeeController employeeController;
    protected final TimeSheetController timeSheetController;
    protected final DocumentsService documentsService;

    @GetMapping("/paysheet/{id}")
    public String getPaySheet(@PathVariable long id, Model model){
        model.addAttribute("PaySheet", paySheetController.getByEmployeeId(id));
        model.addAttribute("Employee", employeeController.get(id));
        model.addAttribute("Employee_id", id);
        return "PaySheet";
    }

    @GetMapping("/paysheet/{id}/downloads/word")
    public ResponseEntity<byte[]> downloadsPaySheetWord(@PathVariable long id,
                                                HttpServletResponse response) throws IOException {
        return ResponseEntity.ok().body(documentsService.generateWordPaySheetDocument(id, response));
    }

    @GetMapping("/paysheet/{id}/downloads/excel")
    public ResponseEntity<byte[]> downloadsPaySheetExcel(@PathVariable long id,
                                                        HttpServletResponse response) throws IOException {
        return ResponseEntity.ok().body(documentsService.generateExcelPaySheetDocument(id, response));
    }

    @GetMapping("/timesheet/{id}")
    public String getTimeSheetReport(@PathVariable long id,
                                     @RequestParam(required = false) Integer year,
                                     @RequestParam(required = false) Byte month,
                                     Model model){
        model.addAttribute("TimeSheet", timeSheetController.searchByYearAndMonth(id, year, month));
        model.addAttribute("Employee", employeeController.get(id));
        model.addAttribute("Employee_id", id);
        return "TimeSheet";
    }

    @GetMapping("/timesheet/{id}/downloads/word")
    public ResponseEntity<byte[]> timesheetDownloadsWord(@PathVariable long id,
                                                HttpServletResponse response) throws IOException {
        return ResponseEntity.ok().body(documentsService.generateWordTimeSheetDocument(id, response));
    }

    @GetMapping("/timesheet/{id}/downloads/excel")
    public ResponseEntity<byte[]> timesheetDownloadsExcel(@PathVariable long id,
                                                 HttpServletResponse response) throws IOException {
        return ResponseEntity.ok().body(documentsService.generateExcelTimeSheetDocument(id, response));
    }
}
