package ru.salarysage.salarysage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.salarysage.salarysage.models.PaySheetModel;

import java.util.List;

@Repository
public interface PaySheetRepository extends JpaRepository<PaySheetModel, Long> {

    List<PaySheetModel> findAllByEmployeeId_Id(long id);

}
