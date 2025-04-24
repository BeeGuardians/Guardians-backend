package com.guardians.domain.user.repository;

import com.guardians.domain.user.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {

    // user_id = PK라서 findById로 충분하지만, 명시적으로 써도 됨
    UserStats findByUserId(Long userId);
}
