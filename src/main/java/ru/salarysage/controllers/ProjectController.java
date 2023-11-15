package ru.salarysage.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.salarysage.models.ProjectModel;
import ru.salarysage.service.ProjectService;

import java.util.Optional;

@RestController
@RequestMapping("/project")
public class ProjectController {

    protected final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectModel create(@RequestBody ProjectModel p){
        return projectService.create(p);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ProjectModel> get(@PathVariable long id){
        return projectService.get(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectModel put(@PathVariable long id, @RequestBody ProjectModel p){
        return projectService.put(id, p);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        projectService.delete(id);
    }
}
