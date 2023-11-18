package ru.salarysage.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.salarysage.service.DocumentsService;
import ru.salarysage.util.CalculationAnalyticsUtil;

import java.io.IOException;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    protected final ProjectController projectController;

    protected final CalculationAnalyticsUtil calculationAnalyticsUtil;

    protected final DocumentsService documentsService;

    public AnalyticsController(ProjectController projectController,
                               CalculationAnalyticsUtil calculationAnalyticsUtil,
                               DocumentsService documentsService) {
        this.projectController = projectController;
        this.calculationAnalyticsUtil = calculationAnalyticsUtil;
        this.documentsService = documentsService;
    }

    @GetMapping("{id}")
    public String getReport(@PathVariable long id, Model model){
        model.addAttribute("Project", projectController.get(id));
        model.addAttribute("Calculation", calculationAnalyticsUtil.calculationTotal(id));
        return "Analytics";
    }

    @GetMapping("/{id}/downloads/word")
    public ResponseEntity<byte[]> downloadWordProjectDocument(@PathVariable long id,
                                                              HttpServletResponse response) throws IOException {
        return ResponseEntity.ok().body(documentsService.generateWordProjectDocument(id, response));
    }

    @GetMapping("/{id}/downloads/excel")
    public ResponseEntity<byte[]> downloadExelProjectDocument(@PathVariable long id,
                                                              HttpServletResponse response) throws IOException {
        return ResponseEntity.ok().body(documentsService.generateExcelProjectDocument(id, response));
    }
}
