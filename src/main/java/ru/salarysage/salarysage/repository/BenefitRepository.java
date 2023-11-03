package ru.salarysage.salarysage.repository;

import org.springframework.stereotype.Repository;
import ru.salarysage.salarysage.models.BenefitModel;

@Repository
public interface BenefitRepository extends BaseRepository<BenefitModel, Long> {

}
