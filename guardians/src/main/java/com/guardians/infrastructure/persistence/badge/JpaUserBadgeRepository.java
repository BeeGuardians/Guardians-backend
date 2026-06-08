package com.guardians.infrastructure.persistence.badge;

import com.guardians.domain.badge.entity.Badge;
import com.guardians.domain.badge.entity.UserBadge;
import com.guardians.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface JpaUserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser(User user);
    boolean existsByUserAndBadge(User user, Badge badge);
    boolean existsByUserAndBadge_Name(User user, String badgeName);

    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user = :user")
    List<UserBadge> findByUserWithBadge(@Param("user") User user);

    @Query("SELECT ub.badge.id FROM UserBadge ub WHERE ub.user.id = :userId")
    List<Long> findBadgeIdsByUserId(@Param("userId") Long userId);
}
