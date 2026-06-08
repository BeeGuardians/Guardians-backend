package com.guardians.domain.wargame.port;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.domain.wargame.entity.SolvedWargame;
import com.guardians.domain.wargame.entity.Wargame;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface SolvedWargamePort {
    boolean existsByUserAndWargame(User user, Wargame wargame);
    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);
    long countByUser(User user);
    long countByUserAndWargameDifficulty(User user, Difficulty difficulty);
    Set<Long> findWargameIdsByUserId(Long userId);
    List<SolvedWargame> findAllWithWargameByUserId(Long userId);
    List<SolvedWargame> findByUserIdWithWargameAndCategory(Long userId);
    List<SolvedWargame> findByUserIdAndCategoryName(Long userId, String categoryName);
    List<Long> findFirstSolverByWargameId(Long wargameId, Pageable pageable);
    List<Object[]> countSolvedByCategory(Long userId);
    List<Object[]> countSolvedCountByUser();
    boolean checkSolved7DaysInARow(Long userId);
    SolvedWargame save(SolvedWargame solvedWargame);
}
