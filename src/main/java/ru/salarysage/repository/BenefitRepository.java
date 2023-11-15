package ru.salarysage.repository;

import org.springframework.stereotype.Repository;
import ru.salarysage.models.BenefitModel;

@Repository
public interface BenefitRepository extends BaseRepository<BenefitModel, Long> {

}
