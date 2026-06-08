package com.guardians.domain.badge.port;

import com.guardians.domain.badge.entity.Badge;
import com.guardians.domain.badge.entity.UserBadge;
import com.guardians.domain.user.entity.User;

import java.util.List;

public interface UserBadgePort {
    List<UserBadge> findByUser(User user);
    List<UserBadge> findByUserWithBadge(User user);
    List<Long> findBadgeIdsByUserId(Long userId);
    boolean existsByUserAndBadge(User user, Badge badge);
    boolean existsByUserAndBadgeName(User user, String badgeName);
    UserBadge save(UserBadge userBadge);
}
