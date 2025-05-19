package com.guardians.service.wargame;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.entity.UserStats;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.user.repository.UserStatsRepository;
import com.guardians.domain.wargame.entity.*;
import com.guardians.domain.wargame.repository.*;
import com.guardians.dto.wargame.req.ReqCreateReviewDto;
import com.guardians.dto.wargame.req.ReqUpdateReviewDto;
import com.guardians.dto.wargame.res.ResReviewListDto;
import com.guardians.dto.wargame.res.ResSubmitFlagDto;
import com.guardians.dto.wargame.res.ResWargameListDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WargameServiceImpl implements WargameService {

    private final WargameRepository wargameRepository;
    private final WargameFlagRepository wargameFlagRepository;
    private final SolvedWargameRepository solvedWargameRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final WargameLikeRepository wargameLikeRepository;
    private final UserStatsRepository userStatsRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public List<ResWargameListDto> getWargameList(Long userId) {
        return wargameRepository.findAll().stream().map(wargame -> {
            boolean solved = false;
            boolean bookmarked = false;
            boolean liked = false;

            if (userId != null) {
                solved = solvedWargameRepository.existsByUserIdAndWargameId(userId, wargame.getId());
                bookmarked = bookmarkRepository.existsByUserIdAndWargameId(userId, wargame.getId());
                liked = wargameLikeRepository.existsByUserIdAndWargameId(userId, wargame.getId());
            }

            return ResWargameListDto.fromEntity(wargame, solved, bookmarked, liked);
        }).collect(Collectors.toList());
    }

    @Override
    public ResWargameListDto getWargameById(Long userId, Long wargameId) {
        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        boolean solved = false;
        boolean bookmarked = false;
        boolean liked = false;
        int score = wargame.getScore();

        if (userId != null) {
            solved = solvedWargameRepository.existsByUserIdAndWargameId(userId, wargameId);
            bookmarked = bookmarkRepository.existsByUserIdAndWargameId(userId, wargameId);
            liked = wargameLikeRepository.existsByUserIdAndWargameId(userId, wargameId);
        }

        return ResWargameListDto.fromEntity(wargame, solved, bookmarked, liked);
    }

    @Override
    @Transactional
    public ResSubmitFlagDto submitFlag(Long userId, Long wargameId, String flag) {
        if (userId == null) throw new CustomException(ErrorCode.NOT_LOGGED_IN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));
        WargameFlag wargameFlag = wargameFlagRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_VALID_ARGUMENT));

        boolean isCorrect = wargameFlag.getFlag().equals(flag);

        if (isCorrect && !solvedWargameRepository.existsByUserAndWargame(user, wargame)) {
            solvedWargameRepository.save(SolvedWargame.builder()
                    .user(user)
                    .wargame(wargame)
                    .solvedAt(LocalDateTime.now())
                    .build());
            long solvedCount = solvedWargameRepository.countByUser(user);
            userStatsRepository.updateSolvedCount(user.getId(), solvedCount);

            int score = wargame.getScore();
            UserStats stats = userStatsRepository.findById(user.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            stats.addScore(score);
        }

        return ResSubmitFlagDto.builder()
                .correct(isCorrect)
                .message(isCorrect ? "정답입니다!" : "틀렸습니다!")
                .build();
    }

    @Transactional
    @Override
    public boolean toggleBookmark(Long userId, Long wargameId) {
        if (userId == null) throw new CustomException(ErrorCode.NOT_LOGGED_IN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        Optional<Bookmark> bookmarkOpt = bookmarkRepository.findByUserAndWargame(user, wargame);

        if (bookmarkOpt.isPresent()) {
            bookmarkRepository.delete(bookmarkOpt.get());
            return false;
        } else {
            bookmarkRepository.save(Bookmark.builder()
                    .user(user)
                    .wargame(wargame)
                    .createdAt(LocalDateTime.now())
                    .build());
            return true;
        }
    }

    @Transactional
    @Override
    public boolean toggleLike(Long userId, Long wargameId) {
        if (userId == null) throw new CustomException(ErrorCode.NOT_LOGGED_IN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        Optional<WargameLike> likeOpt = wargameLikeRepository.findByUserAndWargame(user, wargame);

        if (likeOpt.isPresent()) {
            wargameLikeRepository.delete(likeOpt.get());
            wargame.setLikeCount(Math.max(0, wargame.getLikeCount() - 1));
            wargameRepository.save(wargame);
            return false;
        } else {
            wargameLikeRepository.save(WargameLike.builder()
                    .user(user)
                    .wargame(wargame)
                    .createdAt(LocalDateTime.now())
                    .build());
            wargame.setLikeCount(wargame.getLikeCount() + 1);
            wargameRepository.save(wargame);
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResReviewListDto> getWargameReviews(Long wargameId) {
        wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        return reviewRepository.findAllByWargameIdOrderByCreatedAtAsc(wargameId).stream()
                .map(ResReviewListDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ResReviewListDto createReview(Long userId, Long wargameId, ReqCreateReviewDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        Review review = Review.builder()
                .user(user)
                .wargame(wargame)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0)
                .build();

        return ResReviewListDto.fromEntity(reviewRepository.save(review));
    }

    @Transactional
    @Override
    public ResReviewListDto updateReview(Long userId, Long reviewId, ReqUpdateReviewDto request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_ACCESS));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        review.setContent(request.getContent());
        review.setUpdatedAt(LocalDateTime.now());

        return ResReviewListDto.fromEntity(review);
    }

    @Transactional
    @Override
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_ACCESS));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        reviewRepository.delete(review);
    }
}
