package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.domain.wargame.entity.SolvedWargame;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.port.SolvedWargamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SolvedWargameAdapter implements SolvedWargamePort {

    private final JpaSolvedWargameRepository jpa;

    @Override
    public boolean existsByUserAndWargame(User user, Wargame wargame) {
        return jpa.existsByUserAndWargame(user, wargame);
    }

    @Override
    public boolean existsByUserIdAndWargameId(Long userId, Long wargameId) {
        return jpa.existsByUserIdAndWargameId(userId, wargameId);
    }

    @Override
    public long countByUser(User user) {
        return jpa.countByUser(user);
    }

    @Override
    public long countByUserAndWargameDifficulty(User user, Difficulty difficulty) {
        return jpa.countByUserAndWargame_Difficulty(user, difficulty);
    }

    @Override
    public Set<Long> findWargameIdsByUserId(Long userId) {
        return jpa.findWargameIdsByUserId(userId);
    }

    @Override
    public List<SolvedWargame> findAllWithWargameByUserId(Long userId) {
        return jpa.findAllWithWargameByUserId(userId);
    }

    @Override
    public List<SolvedWargame> findByUserIdWithWargameAndCategory(Long userId) {
        return jpa.findByUserIdWithWargameAndCategory(userId);
    }

    @Override
    public List<SolvedWargame> findByUserIdAndCategoryName(Long userId, String categoryName) {
        return jpa.findByUserIdAndCategoryName(userId, categoryName);
    }

    @Override
    public List<Long> findFirstSolverByWargameId(Long wargameId, Pageable pageable) {
        return jpa.findFirstSolverByWargameId(wargameId, pageable);
    }

    @Override
    public List<Object[]> countSolvedByCategory(Long userId) {
        return jpa.countSolvedByCategory(userId);
    }

    @Override
    public List<Object[]> countSolvedCountByUser() {
        return jpa.countSolvedCountByUser();
    }

    @Override
    public boolean checkSolved7DaysInARow(Long userId) {
        return jpa.checkSolved7DaysInARow(userId);
    }

    @Override
    public SolvedWargame save(SolvedWargame solvedWargame) {
        return jpa.save(solvedWargame);
    }
}
