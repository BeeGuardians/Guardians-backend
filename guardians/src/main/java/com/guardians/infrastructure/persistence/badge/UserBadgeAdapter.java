package com.guardians.infrastructure.persistence.badge;

import com.guardians.domain.badge.entity.Badge;
import com.guardians.domain.badge.entity.UserBadge;
import com.guardians.domain.badge.port.UserBadgePort;
import com.guardians.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserBadgeAdapter implements UserBadgePort {

    private final JpaUserBadgeRepository jpa;

    @Override
    public List<UserBadge> findByUser(User user) {
        return jpa.findByUser(user);
    }

    @Override
    public List<UserBadge> findByUserWithBadge(User user) {
        return jpa.findByUserWithBadge(user);
    }

    @Override
    public List<Long> findBadgeIdsByUserId(Long userId) {
        return jpa.findBadgeIdsByUserId(userId);
    }

    @Override
    public boolean existsByUserAndBadge(User user, Badge badge) {
        return jpa.existsByUserAndBadge(user, badge);
    }

    @Override
    public boolean existsByUserAndBadgeName(User user, String badgeName) {
        return jpa.existsByUserAndBadge_Name(user, badgeName);
    }

    @Override
    public UserBadge save(UserBadge userBadge) {
        return jpa.save(userBadge);
    }
}
