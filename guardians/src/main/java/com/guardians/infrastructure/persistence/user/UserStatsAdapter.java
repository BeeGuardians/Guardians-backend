package com.guardians.infrastructure.persistence.user;

import com.guardians.domain.user.entity.UserStats;
import com.guardians.domain.user.port.UserStatsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserStatsAdapter implements UserStatsPort {

    private final JpaUserStatsRepository jpa;

    @Override
    public Optional<UserStats> findById(Long userId) {
        return jpa.findById(userId);
    }

    @Override
    public Optional<UserStats> findWithUserById(Long userId) {
        return jpa.findWithUserById(userId);
    }

    @Override
    public List<UserStats> findAllWithUserOrderByScoreDesc() {
        return jpa.findAllWithUserOrderByScoreDesc();
    }

    @Override
    public Optional<Integer> findUserRankByUserId(Long userId) {
        return jpa.findUserRankByUserId(userId);
    }

    @Override
    public void updateSolvedCount(Long userId, Long count) {
        jpa.updateSolvedCount(userId, count);
    }
}
