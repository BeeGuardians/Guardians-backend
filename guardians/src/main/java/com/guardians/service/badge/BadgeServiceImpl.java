package com.guardians.service.badge;

import com.guardians.domain.badge.entity.UserBadge;
import com.guardians.domain.badge.repository.BadgeRepository;
import com.guardians.domain.badge.repository.UserBadgeRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.dto.badge.res.ResBadgeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;

    @Override
    public List<ResBadgeDto> getAllBadgesWithUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);
        List<Long> earnedBadgeIds = userBadges.stream()
                .map(ub -> ub.getBadge().getId())
                .collect(Collectors.toList());

        return badgeRepository.findAll().stream()
                .map(badge -> ResBadgeDto.builder()
                        .id(badge.getId())
                        .name(badge.getName())
                        .description(badge.getDescription())
                        .iconUrl(badge.getIconUrl())
                        .earned(earnedBadgeIds.contains(badge.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ResBadgeDto> getEarnedBadges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userBadgeRepository.findByUser(user).stream()
                .map(userBadge -> {
                    var badge = userBadge.getBadge();
                    return ResBadgeDto.builder()
                            .id(badge.getId())
                            .name(badge.getName())
                            .description(badge.getDescription())
                            .iconUrl(badge.getIconUrl())
                            .earned(true)
                            .build();
                })
                .collect(Collectors.toList());
    }

}
