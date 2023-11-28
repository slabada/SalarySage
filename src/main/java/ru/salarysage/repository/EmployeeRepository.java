package ru.salarysage.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.salarysage.dto.EmployeeDTO;
import ru.salarysage.models.EmployeeModel;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeModel, Long> {

    // Пользовательский метод для поиска сотрудников на основе фильтра
    @Query("""
    SELECT x from EmployeeModel x
    WHERE (:#{#filter.lastName} IS NULL OR x.lastName = :#{#filter.lastName})
    and (:#{#filter.firstName} IS NULL OR x.firstName = :#{#filter.firstName})
    and (:#{#filter.address} IS NULL OR x.address = :#{#filter.address})
    and (:#{#filter.position} IS NULL OR x.position = :#{#filter.position})
    """)
    List<EmployeeDTO> search(@Param("filter") EmployeeModel employee, PageRequest page);
}
