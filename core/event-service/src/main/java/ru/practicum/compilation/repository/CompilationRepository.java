package ru.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @EntityGraph(attributePaths = {"events"})
    @Query("""
            Select c From Compilation c
            WHERE c.pinned = :pinned
            """)
    List<Compilation> findCompilationByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}