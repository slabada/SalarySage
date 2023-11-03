package ru.salarysage.salarysage.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.salarysage.models.PaySheetModel;
import ru.salarysage.salarysage.service.PaySheetService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/paysheet")
public class PaySheetController {

    protected final PaySheetService paySheetService;

    public PaySheetController(PaySheetService paySheetService) {
        this.paySheetService = paySheetService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PaySheetModel create(@RequestBody @Valid PaySheetModel ps){
        return paySheetService.create(ps);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<PaySheetModel> create(@PathVariable long id){
        return paySheetService.get(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaySheetModel put(@PathVariable long id, @RequestBody @Valid PaySheetModel ps){
        return paySheetService.put(id,ps);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        paySheetService.delete(id);
    }

    @GetMapping("/employee/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PaySheetModel> getByEmployeeId(@PathVariable long id){
        return paySheetService.getByEmployeeId(id);
    }
}
