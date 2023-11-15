package ru.salarysage.repository;

import org.springframework.stereotype.Repository;
import ru.salarysage.models.RateModel;

@Repository
public interface RateRepository extends BaseRepository<RateModel, Long> {
}
