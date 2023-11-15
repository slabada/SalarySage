package ru.salarysage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

// Абстрактный репозиторий
@NoRepositoryBean
public interface BaseRepository<T,ID> extends JpaRepository<T,ID> {
    boolean existsByName(String name);

    Optional<T> findByName(String name);

    boolean existsByNameAndIdNot(String name, long id);
}
