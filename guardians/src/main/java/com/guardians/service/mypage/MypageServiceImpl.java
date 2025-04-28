package com.guardians.service.mypage;

import com.guardians.domain.board.repository.BoardRepository;
import com.guardians.domain.wargame.repository.ReviewRepository;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.user.repository.UserStatsRepository;
import com.guardians.domain.wargame.repository.BookmarkRepository;
import com.guardians.domain.wargame.repository.SolvedWargameRepository;
import com.guardians.dto.mypage.res.*;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                solvedWargameRepository.findAllByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ResBookmarkDto getBookmarks(Long userId) {
        return ResBookmarkDto.fromEntities(
                bookmarkRepository.findAllByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ResPostDto getPosts(Long userId) {
        return ResPostDto.fromEntities(
                boardRepository.findAllByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ResReviewDto getReviews(Long userId) {
        return ResReviewDto.fromEntities(
                reviewRepository.findAllByUserId(userId)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ResRankDto getRank(Long userId) {
        return userStatsRepository.findById(userId)
                .map(ResRankDto::fromEntity)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
