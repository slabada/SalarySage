package ru.salarysage.salarysage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/report")
public class ReportController {

    protected final PaySheetController paySheetController;

    protected final EmployeeController employeeController;

    protected final TimeSheetController timeSheetController;

    public ReportController(PaySheetController paySheetController,
                            EmployeeController employeeController,
                            TimeSheetController timeSheetController) {
        this.paySheetController = paySheetController;
        this.employeeController = employeeController;
        this.timeSheetController = timeSheetController;
    }

    @GetMapping("/{id}")
    public String getReport(@PathVariable long id, Model model){
        model.addAttribute("PaySheet", paySheetController.getByEmployeeId(id));
        model.addAttribute("Employee", employeeController.get(id));
        return "report";
    }

    @GetMapping("/timesheet/{id}")
    public String getTimeSheetReport(@PathVariable long id,
                                     Integer year,
                                     Byte month,
                                     Model model){
        model.addAttribute("TimeSheet", timeSheetController.searchByYearAndMonth(id, year, month));
        model.addAttribute("Employee", employeeController.get(id));
        return "report_timesheet";
    }
}
