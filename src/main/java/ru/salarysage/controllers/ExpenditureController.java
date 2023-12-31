package ru.salarysage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.dto.ExpenditureDTO;
import ru.salarysage.models.ExpenditureModel;
import ru.salarysage.service.ExpenditureService;

import java.util.Optional;

@RestController
@RequestMapping("/expenditure")
@RequiredArgsConstructor
public class ExpenditureController {

    protected final ExpenditureService expenditureService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenditureDTO create(@RequestBody @Valid ExpenditureModel e){
        return expenditureService.create(e);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ExpenditureDTO> get(@PathVariable long id){
        return expenditureService.get(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExpenditureDTO put(@PathVariable long id, @RequestBody @Valid ExpenditureModel e){
        return expenditureService.put(id,e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void put(@PathVariable long id){
        expenditureService.delete(id);
    }
}
