package com.guardians.service.badge;

import com.guardians.domain.badge.entity.Badge;
import com.guardians.domain.badge.entity.UserBadge;
import com.guardians.domain.badge.repository.BadgeRepository;
import com.guardians.domain.badge.repository.UserBadgeRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.domain.wargame.repository.SolvedWargameRepository;
import com.guardians.dto.badge.res.ResUserBadgeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final SolvedWargameRepository solvedWargameRepository;

    @Override
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
                        .iconUrl(badge.getIconUrl())
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
                            .iconUrl(badge.getIconUrl())
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

        // 입문자: 첫 문제 성공
        if (totalSolved == 1 && !owned.contains("입문자")) {
            assignBadgeIfNeeded(userId, "입문자");
        }

        // 신참 해커: 5문제 이상
        if (totalSolved >= 5 && !owned.contains("신참 해커")) {
            assignBadgeIfNeeded(userId, "신참 해커");
        }

        // 지옥을 맛본 자: HARD 문제 푼 적 있음
        boolean solvedHard = solvedWargameRepository.existsByUserAndWargame_Difficulty(user, Difficulty.HARD);

        if (solvedHard && !owned.contains("지옥을 맛본 자")) {
            assignBadgeIfNeeded(user.getId(), "지옥을 맛본 자");
        }

        // 카테고리별 체크
        Set<String> categories = solvedWargameRepository.findDistinctCategoryNamesByUserId(userId);

        if (categories.contains("Web") && !owned.contains("웹 해커")) {
            assignBadgeIfNeeded(userId, "웹 해커");
        }

        if (categories.contains("Forensic") && !owned.contains("포렌식 달인")) {
            assignBadgeIfNeeded(userId, "포렌식 달인");
        }

        if (categories.contains("Crypto") && !owned.contains("암호 해독자")) {
            assignBadgeIfNeeded(userId, "암호 해독자");
        }

        if (categories.contains("BruteForce") && !owned.contains("무차별 해커")) {
            assignBadgeIfNeeded(userId, "무차별 해커");
        }

        if (categories.contains("SourceLeak") && !owned.contains("정보 침투자")) {
            assignBadgeIfNeeded(userId, "정보 침투자");
        }

        // 탐험가: 모든 카테고리 하나씩
        if (categories.containsAll(List.of("Web", "Forensic", "Crypto", "BruteForce", "SourceLeak"))
                && !owned.contains("탐험가")) {
            assignBadgeIfNeeded(userId, "탐험가");
        }

        // 퍼스트 블러드
        Long firstSolverId = solvedWargameRepository.findFirstSolverId(user.getId());
        if (Objects.equals(firstSolverId, userId) && !owned.contains("퍼스트 블러드")) {
            assignBadgeIfNeeded(userId, "퍼스트 블러드");
        }

        // 꾸준한 해커: 7일 연속
        boolean solved7Days = solvedWargameRepository.checkSolved7DaysInARow(userId);
        if (solved7Days && !owned.contains("꾸준한 해커")) {
            assignBadgeIfNeeded(userId, "꾸준한 해커");
        }
    }

    @Transactional
    public void assignBadgeIfNeeded(Long userId, String badgeName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Badge badge = badgeRepository.findByName(badgeName)
                .orElseThrow(() -> new IllegalArgumentException("Badge not found"));

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
