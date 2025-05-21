package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.dto.wargame.res.ResHotWargameDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WargameRepository extends JpaRepository<Wargame, Long> {

    @Query("""
        SELECT new com.guardians.dto.wargame.res.ResHotWargameDto(
            w.id,
            w.title,
            COUNT(sw)
        )
        FROM SolvedWargame sw
        JOIN sw.wargame w
        GROUP BY w.id, w.title
        ORDER BY COUNT(sw) DESC
    """)
    Page<ResHotWargameDto> findHotWargames(Pageable pageable);

    @Query("SELECT w FROM Wargame w JOIN FETCH w.category")
    List<Wargame> findAllWithCategory();

    @Query("SELECT w FROM Wargame w JOIN FETCH w.category WHERE w.id = :id")
    Optional<Wargame> findByIdWithCategory(@Param("id") Long id);

}
