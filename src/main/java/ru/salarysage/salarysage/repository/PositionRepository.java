package ru.salarysage.salarysage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.salarysage.salarysage.models.PositionModel;

@Repository
public interface PositionRepository extends BaseRepository<PositionModel, Long> {
    // Метод, который проверяет существование позиции по имени.
    boolean existsByName(String name);
}
