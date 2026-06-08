package com.guardians.application.badge;

import com.guardians.domain.badge.entity.Badge;
import com.guardians.domain.badge.entity.BadgeType;
import com.guardians.domain.badge.entity.UserBadge;
import com.guardians.domain.badge.port.BadgePort;
import com.guardians.domain.badge.port.UserBadgePort;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.port.UserPort;
import com.guardians.domain.wargame.entity.CategoryType;
import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.domain.wargame.port.SolvedWargamePort;
import com.guardians.dto.badge.res.ResUserBadgeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeFacade {

    private final BadgePort badgePort;
    private final UserBadgePort userBadgePort;
    private final UserPort userPort;
    private final SolvedWargamePort solvedWargamePort;

    @Transactional
    public List<ResUserBadgeDto> getAllBadgesWithUserStatus(Long userId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        checkAndAssignBadges(user);

        List<UserBadge> userBadges = userBadgePort.findByUser(user);
        List<Long> earnedBadgeIds = userBadges.stream()
                .map(ub -> ub.getBadge().getId())
                .collect(Collectors.toList());

        return badgePort.findAll().stream()
                .map(badge -> ResUserBadgeDto.builder()
                        .id(badge.getId())
                        .name(badge.getName())
                        .description(badge.getDescription())
                        .trueIconUrl(badge.getTrueIconUrl())
                        .falseIconUrl(badge.getFalseIconUrl())
                        .earned(earnedBadgeIds.contains(badge.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ResUserBadgeDto> getEarnedBadges(Long userId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userBadgePort.findByUser(user).stream()
                .map(userBadge -> {
                    var badge = userBadge.getBadge();
                    return ResUserBadgeDto.builder()
                            .id(badge.getId())
                            .name(badge.getName())
                            .description(badge.getDescription())
                            .trueIconUrl(badge.getTrueIconUrl())
                            .falseIconUrl(badge.getFalseIconUrl())
                            .earned(true)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkAndAssignBadges(User user) {
        Long userId = user.getId();
        Set<String> owned = userBadgePort.findByUserWithBadge(user).stream()
                .map(ub -> ub.getBadge().getName())
                .collect(Collectors.toSet());

        long totalSolved = solvedWargamePort.countByUser(user);

        if (totalSolved >= 1 && !owned.contains(BadgeType.BEGINNER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.BEGINNER);
        }

        if (totalSolved >= 5 && !owned.contains(BadgeType.WARGAME_MASTER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.WARGAME_MASTER);
        }

        long hardCount = solvedWargamePort.countByUserAndWargameDifficulty(user, Difficulty.HARD);
        if (hardCount >= 3 && !owned.contains(BadgeType.HELL_SURVIVOR.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.HELL_SURVIVOR);
        }

        Map<String, Long> categoryCountMap = solvedWargamePort.countSolvedByCategory(userId).stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        if (categoryCountMap.getOrDefault(CategoryType.WEB.getDisplayName(), 0L) >= 3
                && !owned.contains(BadgeType.WEB_HACKER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.WEB_HACKER);
        }

        if (categoryCountMap.getOrDefault(CategoryType.FORENSIC.getDisplayName(), 0L) >= 3
                && !owned.contains(BadgeType.DIGITAL_TRACKER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.DIGITAL_TRACKER);
        }

        if (categoryCountMap.getOrDefault(CategoryType.CRYPTO.getDisplayName(), 0L) >= 3
                && !owned.contains(BadgeType.CRYPTO_BREAKER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.CRYPTO_BREAKER);
        }

        if (categoryCountMap.getOrDefault(CategoryType.BRUTE_FORCE.getDisplayName(), 0L) >= 3
                && !owned.contains(BadgeType.BRUTE_FORCER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.BRUTE_FORCER);
        }

        if (categoryCountMap.getOrDefault(CategoryType.SOURCE_LEAK.getDisplayName(), 0L) >= 3
                && !owned.contains(BadgeType.LEAK_INTRUDER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.LEAK_INTRUDER);
        }

        Set<String> allCategories = Set.of(
                CategoryType.WEB.getDisplayName(),
                CategoryType.FORENSIC.getDisplayName(),
                CategoryType.CRYPTO.getDisplayName(),
                CategoryType.BRUTE_FORCE.getDisplayName(),
                CategoryType.SOURCE_LEAK.getDisplayName()
        );
        if (categoryCountMap.keySet().containsAll(allCategories) && !owned.contains(BadgeType.EXPLORER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.EXPLORER);
        }

        boolean solved7Days = solvedWargamePort.checkSolved7DaysInARow(userId);
        if (solved7Days && !owned.contains(BadgeType.DAILY_GRINDER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.DAILY_GRINDER);
        }
    }

    @Transactional
    public void checkAndAssignFirstBloodBadge(Long userId, Long wargameId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean hasFirstBlood = userBadgePort.existsByUserAndBadgeName(user, BadgeType.FIRST_BLOOD.getDisplayName());

        if (hasFirstBlood) {
            return;
        }

        List<Long> firstSolverList = solvedWargamePort.findFirstSolverByWargameId(wargameId, PageRequest.of(0, 1));

        if (firstSolverList.isEmpty()) {
            return;
        }
        Long firstSolverId = firstSolverList.get(0);

        if (Objects.equals(firstSolverId, userId)) {
            assignBadgeIfNeeded(userId, BadgeType.FIRST_BLOOD);
        }
    }

    @Transactional
    public void assignBadgeIfNeeded(Long userId, BadgeType badgeType) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Badge badge = badgePort.findByName(badgeType.getDisplayName())
                .orElseThrow(() -> new IllegalArgumentException("Badge not found: " + badgeType.getDisplayName()));

        if (userBadgePort.existsByUserAndBadge(user, badge)) return;

        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .awardedAt(LocalDateTime.now())
                .build();

        userBadgePort.save(userBadge);
    }
}
