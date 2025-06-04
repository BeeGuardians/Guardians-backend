package com.guardians.service.badge;

import com.guardians.domain.user.entity.User;
import com.guardians.dto.badge.res.ResUserBadgeDto;

import java.util.List;

public interface BadgeService {
    List<ResUserBadgeDto> getAllBadgesWithUserStatus(Long userId);
    List<ResUserBadgeDto> getEarnedBadges(Long userId);
    void checkAndAssignBadges(User user);
}
