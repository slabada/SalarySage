package ru.salarysage.repository;

import org.springframework.web.bind.annotation.RestController;
import ru.salarysage.models.ProjectModel;

@RestController
public interface ProjectRepository extends BaseRepository<ProjectModel, Long> {
}
