package ru.salarysage.repository;

import org.springframework.stereotype.Repository;
import ru.salarysage.models.ExpenditureModel;

@Repository
public interface ExpenditureRepository extends BaseRepository<ExpenditureModel, Long> {
}
