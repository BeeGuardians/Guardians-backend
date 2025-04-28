//package com.guardians.service.badge;
//
//import com.guardians.domain.badge.repository.SolveRepository;
//import com.guardians.domain.user.entity.Badge;
//import com.guardians.domain.user.entity.UserBadge;
//import com.guardians.domain.user.repository.BadgeRepository;
//import com.guardians.domain.user.repository.UserBadgeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class BadgeService {
//
//    private final UserBadgeRepository userBadgeRepository;
//    private final BadgeRepository badgeRepository;
//    private final SolveRepository solveRepository;
//    private final RankingRepository rankingRepository;
//
//    @Transactional
//    public void checkAndAwardBadges(Long userId) {
//        List<UserBadge> existingBadges = userBadgeRepository.findByUserId(userId);
//        Set<Long> awardedBadgeIds = existingBadges.stream()
//                .map(userBadge -> userBadge.getBadge().getId())
//                .collect(Collectors.toSet());
//
//        List<Badge> allBadges = badgeRepository.findAll();
//
//        for (Badge badge : allBadges) {
//            if (awardedBadgeIds.contains(badge.getId())) continue;
//            if (checkEligibility(userId, badge.getName())) {
//                awardBadge(userId, badge);
//            }
//        }
//    }
//
//    private void awardBadge(Long userId, Badge badge) {
//        UserBadge userBadge = UserBadge.builder()
//                .userId(userId)
//                .badge(badge)
//                .awardedAt(LocalDateTime.now())
//                .build();
//        userBadgeRepository.save(userBadge);
//    }
//
//    private boolean checkEligibility(Long userId, String badgeName) {
//        switch (badgeName) {
//            case "입문자":
//                return isBeginner(userId);
//            case "탐험가":
//                return isExplorer(userId);
//            case "초단타 클리어":
//                return isSpeedrunner(userId);
//            case "탐정":
//                return isDetective(userId);
//            case "리버싱 달인":
//                return isReverseMaster(userId);
//            case "웹 해커":
//                return isWebRaider(userId);
//            case "익스플로잇러":
//                return isExploitKing(userId);
//            case "암호 해독자":
//                return isCryptoBreaker(userId);
//            case "퍼스트 블러드":
//                return isFirstBlood(userId);
//            case "지옥을 맛본 자":
//                return isHellSurvivor(userId);
//            case "꾸준한 해커":
//                return isDailyGrinder(userId);
//            case "워게임 마스터":
//                return isWargameMaster(userId);
//            case "워게임 MVP":
//                return isSeasonTop(userId);
//            case "전설의 레전드":
//                return isLegend(userId);
//            default:
//                return false;
//        }
//    }
//
//    // 각 뱃지별 체크 메소드
//
//    private boolean isBeginner(Long userId) {
//        return solveRepository.countSolvedByUser(userId) >= 1;
//    }
//
//    private boolean isExplorer(Long userId) {
//        return solveRepository.hasSolvedAllCategories(userId);
//    }
//
//    private boolean isSpeedrunner(Long userId) {
//        return solveRepository.hasSolvedWithinTenMinutes(userId);
//    }
//
//    private boolean isDetective(Long userId) {
//        return solveRepository.countSolvedByCategory(userId, "Forensics") >= 10;
//    }
//
//    private boolean isReverseMaster(Long userId) {
//        return solveRepository.countSolvedByCategory(userId, "Reverse") >= 10;
//    }
//
//    private boolean isWebRaider(Long userId) {
//        return solveRepository.countSolvedByCategory(userId, "Web") >= 10;
//    }
//
//    private boolean isExploitKing(Long userId) {
//        return solveRepository.countSolvedByCategory(userId, "Pwn") >= 10;
//    }
//
//    private boolean isCryptoBreaker(Long userId) {
//        return solveRepository.countSolvedByCategory(userId, "Crypto") >= 10;
//    }
//
//    private boolean isFirstBlood(Long userId) {
//        return solveRepository.hasFirstBlood(userId);
//    }
//
//    private boolean isHellSurvivor(Long userId) {
//        return solveRepository.hasSolvedHardestLevel(userId);
//    }
//
//    private boolean isDailyGrinder(Long userId) {
//        return solveRepository.hasSolvedEveryday7Days(userId);
//    }
//
//    private boolean isWargameMaster(Long userId) {
//        return solveRepository.countSolvedByUser(userId) >= 50;
//    }
//
//    private boolean isSeasonTop(Long userId) {
//        return rankingRepository.isSeasonTop1(userId);
//    }
//
//    private boolean isLegend(Long userId) {
//        return rankingRepository.isTop1Percent(userId);
//    }
//}
