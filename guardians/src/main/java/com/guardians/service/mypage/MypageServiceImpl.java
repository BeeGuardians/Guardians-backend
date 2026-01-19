package com.guardians.service.mypage;

import com.guardians.domain.board.repository.BoardRepository;
import com.guardians.domain.user.entity.UserStats;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.user.repository.UserStatsRepository;
import com.guardians.domain.wargame.repository.BookmarkRepository;
import com.guardians.domain.wargame.repository.ReviewRepository;
import com.guardians.domain.wargame.repository.SolvedWargameRepository;
import com.guardians.dto.mypage.res.*;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;
    private final SolvedWargameRepository solvedWargameRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BoardRepository boardRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    @Override
    public ResProfileDto getProfile(Long userId) {
        return userRepository.findById(userId)
                .map(ResProfileDto::fromEntity)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public ResSolvedDto getSolvedProblems(Long userId) {
        return ResSolvedDto.fromEntities(
                solvedWargameRepository.findAllWithWargameByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ResBookmarkDto getBookmarks(Long userId) {
        return ResBookmarkDto.fromEntities(
                bookmarkRepository.findAllWithWargameByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ResBoardDto getBoards(Long userId) {
        return ResBoardDto.fromEntities(
                boardRepository.findAllByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ResReviewDto getReviews(Long userId) {
        return ResReviewDto.fromEntities(
                reviewRepository.findAllWithWargameByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ResRankDto getRank(Long userId) {
        return userStatsRepository.findWithUserById(userId)
                .map(stats -> ResRankDto.fromEntity(stats, 0)) // 개별 조회이므로 rank는 임시 0
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResRankDto> getAllRanks() {
        List<UserStats> statsList = userStatsRepository.findAllWithUserOrderByScoreDesc();
        List<ResRankDto> result = new ArrayList<>();

        int currentRank = 1;
        for (UserStats stats : statsList) {
            result.add(ResRankDto.fromEntity(stats, currentRank++));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ResUserStatsDto getUserStats(Long userId) {
        // DB에서 직접 사용자 통계 조회 (N+1 해결)
        UserStats myStats = userStatsRepository.findWithUserById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // DB에서 직접 랭크 계산 (N+1 해결)
        int rank = userStatsRepository.findUserRankByUserId(userId)
                .orElse(0);

        return ResUserStatsDto.builder()
                .score(myStats.getScore())
                .rank(rank)
                .solvedCount(myStats.getTotalSolved())
                .tier(myStats.getTier().name())
                .build();
    }

    @Transactional
    public void updateAllUserSolvedCounts() {
        List<Object[]> counts = solvedWargameRepository.countSolvedCountByUser();

        for (Object[] row : counts) {
            Long userId = (Long) row[0];
            Long solvedCount = (Long) row[1];
            userStatsRepository.updateSolvedCount(userId, solvedCount);
        }
    }
}
