package ru.salarysage.salarysage.repository;

import org.springframework.stereotype.Repository;
import ru.salarysage.salarysage.models.RateModel;

@Repository
public interface RateRepository extends BaseRepository<RateModel, Long> {
}
