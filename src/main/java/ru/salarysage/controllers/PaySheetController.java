package ru.salarysage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.dto.PaySheetDTO;
import ru.salarysage.models.PaySheetModel;
import ru.salarysage.service.PaySheetService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/paysheet")
@RequiredArgsConstructor
public class PaySheetController {

    protected final PaySheetService paySheetService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PaySheetDTO create(@RequestBody @Valid PaySheetModel ps){
        return paySheetService.create(ps);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<PaySheetDTO> create(@PathVariable long id){
        return paySheetService.get(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaySheetDTO put(@PathVariable long id, @RequestBody @Valid PaySheetModel ps){
        return paySheetService.put(id,ps);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        paySheetService.delete(id);
    }

    @GetMapping("/employee/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PaySheetDTO> getByEmployeeId(@PathVariable long id){
        return paySheetService.getAll(id);
    }
}
