package com.guardians.domain.wargame.repository;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.domain.wargame.entity.SolvedWargame;
import com.guardians.domain.wargame.entity.SolvedWargameId;
import com.guardians.domain.wargame.entity.Wargame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface SolvedWargameRepository extends JpaRepository<SolvedWargame, SolvedWargameId> {
    List<SolvedWargame> findAllByUserId(Long userId); // 추가
    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);
    List<SolvedWargame> findByUser(User user);
    Long countByWargame(Wargame wargame);
    boolean existsByUserAndWargame(User user, Wargame wargame);
    List<SolvedWargame> findByWargame(Wargame wargame);
    List<SolvedWargame> findByUser_Id(Long userId);
    boolean existsByUser_IdAndWargame_Id(Long userId, Long wargameId);
    Long countByUser(User user);

    // 뱃지 관련
    @Query("SELECT DISTINCT w.category.name FROM SolvedWargame sw JOIN sw.wargame w WHERE sw.user.id = :userId")
    Set<String> findDistinctCategoryNamesByUserId(@Param("userId") Long userId);

    boolean existsByUserAndWargame_Difficulty(User user, Difficulty difficulty);

    @Query("SELECT s.user.id FROM SolvedWargame s WHERE s.wargame.id = :wargameId ORDER BY s.solvedAt ASC LIMIT 1")
    Long findFirstSolverId(@Param("wargameId") Long wargameId);

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

}
