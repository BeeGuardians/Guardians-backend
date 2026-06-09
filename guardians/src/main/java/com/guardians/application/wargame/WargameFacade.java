package com.guardians.application.wargame;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.entity.UserStats;
import com.guardians.domain.user.port.UserPort;
import com.guardians.domain.user.port.UserStatsPort;
import com.guardians.domain.wargame.entity.*;
import com.guardians.domain.wargame.port.*;
import com.guardians.domain.wargame.service.WargameDomainService;
import com.guardians.dto.wargame.res.*;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import io.fabric8.kubernetes.api.model.Pod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WargameFacade {

    private final WargamePort wargamePort;
    private final WargameFlagPort wargameFlagPort;
    private final SolvedWargamePort solvedWargamePort;
    private final BookmarkPort bookmarkPort;
    private final WargameLikePort wargameLikePort;
    private final ReviewPort reviewPort;
    private final CategoryPort categoryPort;
    private final KubernetesPodPort kubernetesPodPort;
    private final UserPort userPort;
    private final UserStatsPort userStatsPort;
    private final WargameDomainService wargameDomainService;
    private final com.guardians.application.badge.BadgeFacade badgeFacade;

    @Transactional
    public ResWargameListDto createWargame(String title, String description, Difficulty difficulty, int score,
                                           Long categoryId, String dockerImageUrl, String fileUrl, String flag, Long adminId) {
        User admin = userPort.findById(adminId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!admin.isAdmin()) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        Category category = categoryPort.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_VALID_ARGUMENT));

        Wargame wargame = Wargame.builder()
                .title(title)
                .description(description)
                .difficulty(difficulty)
                .score(score)
                .dockerImageUrl(dockerImageUrl)
                .fileUrl(fileUrl)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        wargamePort.save(wargame);

        WargameFlag wargameFlag = WargameFlag.builder()
                .wargame(wargame)
                .flag(flag)
                .build();

        wargameFlagPort.save(wargameFlag);

        return ResWargameListDto.fromEntity(wargame, false, false, false);
    }

    @Transactional
    public void deleteWargame(Long wargameId) {
        Wargame wargame = wargamePort.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));
        wargamePort.delete(wargame);
    }

    @Transactional
    public List<ResWargameListDto> getWargameList(Long userId) {
        List<Wargame> wargames = wargamePort.findAllWithCategory();
        List<Long> wargameIds = wargames.stream().map(Wargame::getId).toList();

        Set<Long> solvedIds;
        Set<Long> bookmarkedIds;
        Set<Long> likedIds;

        if (userId != null) {
            solvedIds = solvedWargamePort.findWargameIdsByUserId(userId);
            bookmarkedIds = bookmarkPort.findWargameIdsByUserId(userId);
            likedIds = wargameLikePort.findWargameIdsByUserId(userId);
        } else {
            solvedIds = new HashSet<>();
            bookmarkedIds = new HashSet<>();
            likedIds = new HashSet<>();
        }

        // flag N+1 방지
        List<WargameFlag> flags = wargameFlagPort.findAllByWargameIdIn(wargameIds);
        Map<Long, String> flagMap = flags.stream()
                .collect(Collectors.toMap(f -> f.getWargame().getId(), WargameFlag::getFlag));

        return wargames.stream()
                .map(w -> {
                    Long id = w.getId();
                    return ResWargameListDto.fromEntity(
                            w,
                            solvedIds.contains(id),
                            bookmarkedIds.contains(id),
                            likedIds.contains(id)
                    );
                }).toList();
    }

    public ResWargameListDto getWargameById(Long userId, Long wargameId) {
        Wargame wargame = wargamePort.findByIdWithCategory(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        boolean solved = false;
        boolean bookmarked = false;
        boolean liked = false;

        if (userId != null) {
            solved = solvedWargamePort.existsByUserIdAndWargameId(userId, wargameId);
            bookmarked = bookmarkPort.existsByUserIdAndWargameId(userId, wargameId);
            liked = wargameLikePort.existsByUserIdAndWargameId(userId, wargameId);
        }

        return ResWargameListDto.fromEntity(wargame, solved, bookmarked, liked);
    }

    @Transactional
    public ResSubmitFlagDto submitFlag(Long userId, Long wargameId, String flag) {
        if (userId == null) throw new CustomException(ErrorCode.NOT_LOGGED_IN);

        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Wargame wargame = wargamePort.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));
        WargameFlag wargameFlag = wargameFlagPort.findByWargameId(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_VALID_ARGUMENT));

        boolean isCorrect = wargameDomainService.isCorrectFlag(wargameFlag.getFlag(), flag);

        if (isCorrect && !solvedWargamePort.existsByUserAndWargame(user, wargame)) {
            solvedWargamePort.save(SolvedWargame.builder()
                    .user(user)
                    .wargame(wargame)
                    .solvedAt(LocalDateTime.now())
                    .build());
            long solvedCount = solvedWargamePort.countByUser(user);
            userStatsPort.updateSolvedCount(user.getId(), solvedCount);

            int score = wargame.getScore();
            UserStats stats = userStatsPort.findById(user.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            stats.addScore(score);

            badgeFacade.checkAndAssignFirstBloodBadge(user.getId(), wargame.getId());
            badgeFacade.checkAndAssignBadges(user);
        }

        return ResSubmitFlagDto.builder()
                .correct(isCorrect)
                .message(isCorrect ? "정답입니다!" : "틀렸습니다!")
                .build();
    }

    @Transactional
    public boolean toggleBookmark(Long userId, Long wargameId) {
        if (userId == null) throw new CustomException(ErrorCode.NOT_LOGGED_IN);

        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Wargame wargame = wargamePort.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        return bookmarkPort.findByUserAndWargame(user, wargame)
                .map(existing -> {
                    bookmarkPort.delete(existing);
                    return false;
                })
                .orElseGet(() -> {
                    bookmarkPort.save(Bookmark.builder()
                            .user(user)
                            .wargame(wargame)
                            .createdAt(LocalDateTime.now())
                            .build());
                    return true;
                });
    }

    @Transactional
    public boolean toggleLike(Long userId, Long wargameId) {
        if (userId == null) throw new CustomException(ErrorCode.NOT_LOGGED_IN);

        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Wargame wargame = wargamePort.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        return wargameLikePort.findByUserAndWargame(user, wargame)
                .map(existing -> {
                    wargameLikePort.delete(existing);
                    wargame.decreaseLikeCount();
                    return false;
                })
                .orElseGet(() -> {
                    wargameLikePort.save(WargameLike.builder()
                            .user(user)
                            .wargame(wargame)
                            .createdAt(LocalDateTime.now())
                            .build());
                    wargame.increaseLikeCount();
                    return true;
                });
    }

    public List<ResReviewListDto> getWargameReviews(Long wargameId) {
        wargamePort.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        return reviewPort.findAllByWargameIdOrderByCreatedAtAsc(wargameId).stream()
                .map(ResReviewListDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResReviewListDto createReview(Long userId, Long wargameId, String content) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Wargame wargame = wargamePort.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        Review review = Review.builder()
                .user(user)
                .wargame(wargame)
                .content(content)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likeCount(0)
                .build();

        return ResReviewListDto.fromEntity(reviewPort.save(review));
    }

    @Transactional
    public ResReviewListDto updateReview(Long userId, Long reviewId, String content) {
        Review review = reviewPort.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_ACCESS));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        review.updateContent(content);
        return ResReviewListDto.fromEntity(review);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewPort.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED_ACCESS));

        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        reviewPort.delete(review);
    }

    public List<ResHotWargameDto> getHotWargames() {
        Pageable top10 = PageRequest.of(0, 10);
        return wargamePort.findHotWargames(top10).getContent();
    }

    public List<ResUserStatusDto> getActiveUsersByWargame(Long wargameId) {
        String namespace = "ns-wargame";
        List<Pod> pods = kubernetesPodPort.getRunningPodsByWargameId(wargameId, namespace);

        return pods.stream().map(pod -> {
            String podName = pod.getMetadata().getName();
            String[] parts = podName.split("-");

            Long podUserId;
            try {
                podUserId = Long.parseLong(parts[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                return null;
            }

            User user = userPort.findById(podUserId).orElse(null);
            if (user == null) return null;

            return new ResUserStatusDto(
                    user.getUsername(),
                    pod.getMetadata().getCreationTimestamp(),
                    false
            );
        }).filter(Objects::nonNull).toList();
    }

    public String getWargameFlag(Long wargameId) {
        WargameFlag wargameFlag = wargameFlagPort.findByWargameId(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PASSWORD));
        return wargameFlag.getFlag();
    }
}
