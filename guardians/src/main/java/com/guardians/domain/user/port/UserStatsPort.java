package com.guardians.domain.user.port;

import com.guardians.domain.user.entity.UserStats;

import java.util.List;
import java.util.Optional;

public interface UserStatsPort {
    Optional<UserStats> findById(Long userId);
    Optional<UserStats> findWithUserById(Long userId);
    List<UserStats> findAllWithUserOrderByScoreDesc();
    Optional<Integer> findUserRankByUserId(Long userId);
    void updateSolvedCount(Long userId, Long count);
}
