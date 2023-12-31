package ru.salarysage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.service.EmployeeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    protected final EmployeeService employeeService;
    // Обработчик HTTP POST запроса для создания нового сотрудника
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO create(@RequestBody @Valid EmployeeModel employee){
        return employeeService.create(employee);
    }

    // Обработчик HTTP GET запроса для получения информации о сотруднике по его идентификатору
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<EmployeeDTO> get(@PathVariable long id){
        return employeeService.get(id);
    }

    // Обработчик HTTP PUT запроса для обновления информации о сотруднике по его идентификатору
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDTO put(@PathVariable long id,
                           @RequestBody @Valid EmployeeModel employee){
        return employeeService.put(id,employee);
    }

    // Обработчик HTTP DELETE запроса для удаления сотрудника по его идентификатору
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        employeeService.delete(id);
    }

    // Обработчик HTTP GET запроса для поиска сотрудников с заданными параметрами
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDTO> search(@ModelAttribute EmployeeModel e,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size){
        return employeeService.search(e,from,size);
    }
}
