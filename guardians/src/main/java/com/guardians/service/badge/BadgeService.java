package com.guardians.service.badge;

import com.guardians.dto.badge.res.ResBadgeDto;

import java.util.List;

public interface BadgeService {
    List<ResBadgeDto> getAllBadgesWithUserStatus(Long userId);
    List<ResBadgeDto> getEarnedBadges(Long userId);
}
