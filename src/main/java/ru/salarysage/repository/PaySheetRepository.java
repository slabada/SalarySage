package ru.salarysage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.salarysage.dto.PaySheetDTO;
import ru.salarysage.models.PaySheetModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaySheetRepository extends JpaRepository<PaySheetModel, Long> {

    List<PaySheetDTO> findAllByEmployeeId_Id(long id);

    Optional<PaySheetDTO> findById(long id);

}
