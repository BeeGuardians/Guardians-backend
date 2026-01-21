package com.guardians.service.badge;

import com.guardians.domain.badge.entity.Badge;
import com.guardians.domain.badge.entity.BadgeType;
import com.guardians.domain.badge.entity.UserBadge;
import com.guardians.domain.badge.repository.BadgeRepository;
import com.guardians.domain.badge.repository.UserBadgeRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.wargame.entity.CategoryType;
import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.domain.wargame.repository.SolvedWargameRepository;
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
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final SolvedWargameRepository solvedWargameRepository;

    @Override
    @Transactional
    public List<ResUserBadgeDto> getAllBadgesWithUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 뱃지 체크 및 부여 로직 호출
        checkAndAssignBadges(user);

        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);
        List<Long> earnedBadgeIds = userBadges.stream()
                .map(ub -> ub.getBadge().getId())
                .collect(Collectors.toList());

        return badgeRepository.findAll().stream()
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

    @Override
    @Transactional
    public List<ResUserBadgeDto> getEarnedBadges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userBadgeRepository.findByUser(user).stream()
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

    @Override
    @Transactional
    public void checkAndAssignBadges(User user) {
        Long userId = user.getId();
        Set<String> owned = userBadgeRepository.findByUserWithBadge(user).stream()
                .map(ub -> ub.getBadge().getName())
                .collect(Collectors.toSet());

        long totalSolved = solvedWargameRepository.countByUser(user);

        if (totalSolved >= 1 && !owned.contains(BadgeType.BEGINNER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.BEGINNER);
        }

        // 신참 해커: 5문제 이상
        if (totalSolved >= 5 && !owned.contains(BadgeType.WARGAME_MASTER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.WARGAME_MASTER);
        }

        // 지옥을 맛본 자: HARD 3문제 이상 해결
        long hardCount = solvedWargameRepository.countByUserAndWargame_Difficulty(user, Difficulty.HARD);

        if (hardCount >= 3 && !owned.contains(BadgeType.HELL_SURVIVOR.getDisplayName())) {
            assignBadgeIfNeeded(user.getId(), BadgeType.HELL_SURVIVOR);
        }

        // 카테고리별 풀이 수를 한 번의 쿼리로 조회 (N+1 해결)
        Map<String, Long> categoryCountMap = solvedWargameRepository.countSolvedByCategory(userId).stream()
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

        // 탐험가: 모든 카테고리 하나씩 (keySet으로 확인)
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

        // 꾸준한 해커: 7일 연속
        boolean solved7Days = solvedWargameRepository.checkSolved7DaysInARow(userId);
        if (solved7Days && !owned.contains(BadgeType.DAILY_GRINDER.getDisplayName())) {
            assignBadgeIfNeeded(userId, BadgeType.DAILY_GRINDER);
        }
    }

    @Override
    @Transactional
    public void checkAndAssignFirstBloodBadge(Long userId, Long wargameId) {
        // 첫 해결자인지 확인하기 전에, 해당 유저가 '퍼스트 블러드' 뱃지를 이미 가지고 있는지 먼저 확인합니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean hasFirstBlood = userBadgeRepository.existsByUserAndBadge_Name(user, BadgeType.FIRST_BLOOD.getDisplayName());

        // 이미 뱃지가 있다면 더 이상 진행하지 않습니다.
        // (참고: 문제마다 퍼스트블러드를 주고 싶다면 이 로직은 변경되어야 합니다.)
        if (hasFirstBlood) {
            return;
        }

        // wargameId를 이용해 해당 문제를 가장 먼저 푼 유저의 ID를 조회합니다.
        List<Long> firstSolverList = solvedWargameRepository.findFirstSolverByWargameId(wargameId, PageRequest.of(0, 1));

        // 조회 결과가 없으면 종료 (정상적인 경우, 방금 푼 사용자가 있으므로 비어있지 않아야 함)
        if (firstSolverList.isEmpty()) {
            return;
        }
        Long firstSolverId = firstSolverList.get(0);

        // 첫 해결자와 현재 유저가 동일하다면 뱃지를 부여합니다.
        if (Objects.equals(firstSolverId, userId)) {
            assignBadgeIfNeeded(userId, BadgeType.FIRST_BLOOD);
        }
    }

    @Transactional
    public void assignBadgeIfNeeded(Long userId, BadgeType badgeType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Badge badge = badgeRepository.findByName(badgeType.getDisplayName())
                .orElseThrow(() -> new IllegalArgumentException("Badge not found: " + badgeType.getDisplayName()));

        boolean alreadyOwned = userBadgeRepository.existsByUserAndBadge(user, badge);
        if (alreadyOwned) return;

        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .awardedAt(LocalDateTime.now())
                .build();

        userBadgeRepository.save(userBadge);
    }



}
