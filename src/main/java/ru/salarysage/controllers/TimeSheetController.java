package ru.salarysage.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.models.TimeSheetModel;
import ru.salarysage.service.DocumentsService;
import ru.salarysage.service.TimeSheetService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/timesheet")
public class TimeSheetController {

    protected final TimeSheetService timeSheetService;

    public TimeSheetController(TimeSheetService timeSheetService) {
        this.timeSheetService = timeSheetService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeSheetModel create(@RequestBody @Valid TimeSheetModel t){
        return timeSheetService.create(t);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<TimeSheetModel> get(@PathVariable long id){
        return timeSheetService.get(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TimeSheetModel put(@PathVariable long id, @RequestBody @Valid TimeSheetModel t){
        return timeSheetService.put(id, t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        timeSheetService.delete(id);
    }

    @GetMapping("/employee/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<TimeSheetModel> searchByYearAndMonth(@PathVariable long id,
                                                     @RequestParam(required = false) Integer year,
                                                     @RequestParam(required = false) Byte month){
        return timeSheetService.searchByYearAndMonth(id,year,month);
    }

    @GetMapping("/all/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<TimeSheetModel> getAll(@PathVariable long id){
        return timeSheetService.getAll(id);
    }
}
