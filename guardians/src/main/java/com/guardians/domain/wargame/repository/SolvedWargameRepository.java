package com.guardians.domain.wargame.repository;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.domain.wargame.entity.SolvedWargame;
import com.guardians.domain.wargame.entity.SolvedWargameId;
import com.guardians.domain.wargame.entity.Wargame;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface SolvedWargameRepository extends JpaRepository<SolvedWargame, SolvedWargameId> {
    List<SolvedWargame> findAllByUserId(Long userId);

    @Query("SELECT sw FROM SolvedWargame sw JOIN FETCH sw.wargame WHERE sw.user.id = :userId")
    List<SolvedWargame> findAllWithWargameByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);
    boolean existsByUserAndWargame(User user, Wargame wargame);
    Long countByUser(User user);

    @Query("SELECT sw.wargame.id FROM SolvedWargame sw WHERE sw.user.id = :userId")
    Set<Long> findWargameIdsByUserId(@Param("userId") Long userId);

    // 뱃지 관련
    @Query("SELECT DISTINCT w.category.name FROM SolvedWargame sw JOIN sw.wargame w WHERE sw.user.id = :userId")
    Set<String> findDistinctCategoryNamesByUserId(@Param("userId") Long userId);

    boolean existsByUserAndWargame_Difficulty(User user, Difficulty difficulty);

    @Query("SELECT s.user.id FROM SolvedWargame s WHERE s.wargame.id = :wargameId ORDER BY s.solvedAt ASC")
    List<Long> findFirstSolverId(@Param("wargameId") Long wargameId, Pageable pageable);

    @Query(value = """
    SELECT CASE
             WHEN COUNT(DISTINCT DATE(solved_at)) = 7
             THEN TRUE ELSE FALSE
           END
    FROM solved_wargames
    WHERE user_id = :userId
      AND solved_at >= CURRENT_DATE - INTERVAL '6 days'
""", nativeQuery = true)
    boolean checkSolved7DaysInARow(@Param("userId") Long userId);


    @Query("SELECT sw.user.id, COUNT(sw) FROM SolvedWargame sw GROUP BY sw.user.id")
    List<Object[]> countSolvedCountByUser();

    long countByUserAndWargame_Difficulty(User user, Difficulty difficulty);

    @Query("""
    SELECT sw FROM SolvedWargame sw
    JOIN FETCH sw.wargame w
    JOIN FETCH w.category c
    WHERE sw.user.id = :userId AND c.name = :categoryName
""")
    List<SolvedWargame> findByUserIdAndCategoryName(
            @Param("userId") Long userId,
            @Param("categoryName") String categoryName
    );

    @Query("SELECT s FROM SolvedWargame s " +
            "JOIN FETCH s.wargame w " +
            "JOIN FETCH w.category " +
            "WHERE s.user.id = :userId")
    List<SolvedWargame> findByUserIdWithWargameAndCategory(@Param("userId") Long userId);
}
