package ru.salarysage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.dto.TimeSheetDTO;
import ru.salarysage.models.TimeSheetModel;
import ru.salarysage.service.TimeSheetService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/timesheet")
@RequiredArgsConstructor
public class TimeSheetController {

    protected final TimeSheetService timeSheetService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeSheetDTO create(@RequestBody @Valid TimeSheetModel t){
        return timeSheetService.create(t);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<TimeSheetDTO> get(@PathVariable long id){
        return timeSheetService.get(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TimeSheetDTO put(@PathVariable long id, @RequestBody @Valid TimeSheetModel t){
        return timeSheetService.put(id, t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        timeSheetService.delete(id);
    }

    @GetMapping("/employee/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<TimeSheetDTO> searchByYearAndMonth(@PathVariable long id,
                                                     @RequestParam(required = false) Integer year,
                                                     @RequestParam(required = false) Byte month){
        return timeSheetService.searchByYearAndMonth(id,year,month);
    }

    @GetMapping("/all/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<TimeSheetDTO> getAll(@PathVariable long id){
        return timeSheetService.getAll(id);
    }
}
