package ru.salarysage.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.service.ExpenditureService;

import java.util.Optional;

@RestController
@RequestMapping("/expenditure")
public class ExpenditureController {

    protected final ExpenditureService expenditureService;

    public ExpenditureController(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenditureModel create(@RequestBody @Valid ExpenditureModel e){
        return expenditureService.create(e);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ExpenditureModel> get(@PathVariable long id){
        return expenditureService.get(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExpenditureModel put(@PathVariable long id, @RequestBody @Valid ExpenditureModel e){
        return expenditureService.put(id,e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void put(@PathVariable long id){
        expenditureService.delete(id);
    }
}
