package ru.salarysage.repository;

import org.springframework.stereotype.Repository;
import ru.salarysage.models.PositionModel;

@Repository
public interface PositionRepository extends BaseRepository<PositionModel, Long> {
    // Метод, который проверяет существование позиции по имени.
    boolean existsByName(String name);
}
