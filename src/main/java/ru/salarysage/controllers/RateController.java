package ru.salarysage.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.dto.RateDTO;
import ru.salarysage.models.RateModel;
import ru.salarysage.service.RateService;

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
    public RateDTO create(@RequestBody @Valid RateModel r){
        return rateService.create(r);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<RateDTO> get(@PathVariable long id){
        return rateService.get(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RateDTO put(@PathVariable long id, @RequestBody @Valid RateModel r){
        return rateService.put(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        rateService.delete(id);
    }
}
