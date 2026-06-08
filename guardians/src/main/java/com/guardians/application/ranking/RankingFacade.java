package com.guardians.application.ranking;

import com.guardians.domain.board.port.BoardPort;
import com.guardians.domain.user.entity.UserStats;
import com.guardians.domain.user.port.UserPort;
import com.guardians.domain.user.port.UserStatsPort;
import com.guardians.domain.wargame.entity.SolvedWargame;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.port.BookmarkPort;
import com.guardians.domain.wargame.port.ReviewPort;
import com.guardians.domain.wargame.port.SolvedWargamePort;
import com.guardians.domain.wargame.port.WargamePort;
import com.guardians.dto.dashboard.ResRadarChartDto;
import com.guardians.dto.dashboard.ResScoreTrendDto;
import com.guardians.dto.dashboard.ResSolvedTimelineDto;
import com.guardians.dto.mypage.res.*;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingFacade {

    private final UserPort userPort;
    private final UserStatsPort userStatsPort;
    private final SolvedWargamePort solvedWargamePort;
    private final BookmarkPort bookmarkPort;
    private final BoardPort boardPort;
    private final ReviewPort reviewPort;
    private final WargamePort wargamePort;

    @Transactional(readOnly = true)
    public ResProfileDto getProfile(Long userId) {
        return userPort.findById(userId)
                .map(ResProfileDto::fromEntity)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ResSolvedDto getSolvedProblems(Long userId) {
        return ResSolvedDto.fromEntities(
                solvedWargamePort.findAllWithWargameByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    public ResBookmarkDto getBookmarks(Long userId) {
        return ResBookmarkDto.fromEntities(
                bookmarkPort.findAllWithWargameByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    public ResBoardDto getBoards(Long userId) {
        return ResBoardDto.fromEntities(
                boardPort.findAllByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    public ResReviewDto getReviews(Long userId) {
        return ResReviewDto.fromEntities(
                reviewPort.findAllWithWargameByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    public ResRankDto getRank(Long userId) {
        return userStatsPort.findWithUserById(userId)
                .map(stats -> ResRankDto.fromEntity(stats, 0))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ResRankDto> getAllRanks() {
        List<UserStats> statsList = userStatsPort.findAllWithUserOrderByScoreDesc();
        List<ResRankDto> result = new ArrayList<>();

        int currentRank = 1;
        for (UserStats stats : statsList) {
            result.add(ResRankDto.fromEntity(stats, currentRank++));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public ResUserStatsDto getUserStats(Long userId) {
        UserStats myStats = userStatsPort.findWithUserById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        int rank = userStatsPort.findUserRankByUserId(userId).orElse(0);

        return ResUserStatsDto.builder()
                .score(myStats.getScore())
                .rank(rank)
                .solvedCount(myStats.getTotalSolved())
                .tier(myStats.getTier().name())
                .build();
    }

    @Transactional
    public void updateAllUserSolvedCounts() {
        List<Object[]> counts = solvedWargamePort.countSolvedCountByUser();
        for (Object[] row : counts) {
            Long userId = (Long) row[0];
            Long solvedCount = (Long) row[1];
            userStatsPort.updateSolvedCount(userId, solvedCount);
        }
    }

    // Dashboard

    @Transactional(readOnly = true)
    public List<ResRadarChartDto.CategoryScore> calculateRadarChart(Long userId) {
        List<String> categories = List.of("Web", "Crypto", "Forensic", "BruteForce", "SourceLeak");
        List<ResRadarChartDto.CategoryScore> scores = new ArrayList<>();

        for (String category : categories) {
            List<Wargame> allProblems = wargamePort.findByCategoryName(category);
            List<SolvedWargame> userSolved = solvedWargamePort.findByUserIdAndCategoryName(userId, category);

            int totalScore = allProblems.stream().mapToInt(Wargame::getScore).sum();
            int totalCount = allProblems.size();

            int userScore = userSolved.stream().mapToInt(sw -> sw.getWargame().getScore()).sum();
            int userCount = userSolved.size();

            double raw = userScore * (1 + Math.log(Math.max(1, userCount)));
            double maxRaw = totalScore * (1 + Math.log(Math.max(1, totalCount)));

            double normalized = maxRaw == 0 ? 0 : (raw / maxRaw) * 100;

            scores.add(ResRadarChartDto.CategoryScore.builder()
                    .category(category)
                    .normalizedScore(normalized)
                    .build());
        }

        return scores;
    }

    @Transactional(readOnly = true)
    public List<ResSolvedTimelineDto> getSolvedTimeline(Long userId) {
        return solvedWargamePort.findByUserIdWithWargameAndCategory(userId).stream()
                .map(ResSolvedTimelineDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResScoreTrendDto> getScoreTrend(Long userId) {
        List<SolvedWargame> solvedList = solvedWargamePort.findAllWithWargameByUserId(userId);

        Map<LocalDate, Integer> scoreByDate = solvedList.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getSolvedAt().toLocalDate(),
                        Collectors.summingInt(s -> s.getWargame().getScore())
                ));

        return scoreByDate.entrySet().stream()
                .map(entry -> new ResScoreTrendDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
