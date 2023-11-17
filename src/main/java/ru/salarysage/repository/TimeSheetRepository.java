package ru.salarysage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.salarysage.models.EmployeeModel;
import ru.salarysage.models.TimeSheetModel;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheetModel, Long> {
    boolean existsByDateAndEmployeeIdAndIdNot(LocalDate date, EmployeeModel employeeId, long id);

    boolean existsByDateAndEmployeeId(LocalDate date, EmployeeModel employeeId);

    List<TimeSheetModel> findAllByEmployeeId_Id(long id);

    @Query("""
            SELECT e FROM TimeSheetModel e
            WHERE (:year IS NULL OR year(e.date) = :year)
            AND (:month IS NULL OR month(e.date) = :month)
            AND e.employeeId.id = :employeeId
            """)
    List<TimeSheetModel> findAllByYearAndMonth(@Param("year") Integer year,
                                               @Param("month") Byte month,
                                               @Param("employeeId") long employeeId
    );
}
