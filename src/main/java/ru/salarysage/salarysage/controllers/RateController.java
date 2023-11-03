package ru.salarysage.salarysage.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.salarysage.models.RateModel;
import ru.salarysage.salarysage.service.RateService;

import java.util.Optional;

@RestController
@RequestMapping("/rate")
public class RateController {

    protected final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public RateModel create(@RequestBody @Valid RateModel r){
        return rateService.create(r);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<RateModel> get(@PathVariable long id){
        return rateService.get(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RateModel put(@PathVariable long id, @RequestBody @Valid RateModel r){
        return rateService.put(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        rateService.delete(id);
    }
}
