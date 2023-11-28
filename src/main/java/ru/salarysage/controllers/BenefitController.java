package ru.salarysage.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.dto.BenefitDTO;
import ru.salarysage.models.BenefitModel;
import ru.salarysage.service.BenefitService;

import java.util.Optional;

@RestController
@RequestMapping("/benefit")
public class BenefitController {

    protected final BenefitService benefitService;

    public BenefitController(BenefitService benefitService) {
        this.benefitService = benefitService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BenefitDTO create(@RequestBody @Valid BenefitModel b){
        return benefitService.create(b);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<BenefitDTO> get(@PathVariable long id){
        return benefitService.get(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BenefitDTO put(@PathVariable long id, @RequestBody @Valid BenefitModel b){
        return benefitService.put(id, b);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        benefitService.delete(id);
    }
}
