package ru.salarysage.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.salarysage.service.DocumentsService;

import java.io.IOException;

@RestController
@RequestMapping("/downloads")
public class DocumentsController {

    protected final DocumentsService documentsService;

    public DocumentsController(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

//    @GetMapping("/word")
//    @ResponseStatus(HttpStatus.OK)
//    public byte[] downloadWordDocument(HttpServletResponse response) throws IOException {
//        return documentsService.generateWordProjectDocument(response);
//    }
}
