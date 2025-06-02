package com.guardians.service.wargame;

import com.guardians.domain.badge.repository.BadgeRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.entity.UserStats;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.user.repository.UserStatsRepository;
import com.guardians.domain.wargame.entity.*;
import com.guardians.domain.wargame.repository.*;
import com.guardians.dto.wargame.req.ReqCreateReviewDto;
import com.guardians.dto.wargame.req.ReqCreateWargameDto;
import com.guardians.dto.wargame.req.ReqUpdateReviewDto;
import com.guardians.dto.wargame.res.*;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import com.guardians.service.badge.BadgeService;
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
public class WargameServiceImpl implements WargameService {

    private final WargameRepository wargameRepository;
    private final WargameFlagRepository wargameFlagRepository;
    private final SolvedWargameRepository solvedWargameRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final WargameLikeRepository wargameLikeRepository;
    private final UserStatsRepository userStatsRepository;
    private final ReviewRepository reviewRepository;
    private final KubernetesPodService kubernetesPodService;
    private final BadgeService badgeService;
    private final CategoryRepository categoryRepository;


    @Transactional
    @Override
    public ResWargameListDto createWargame(ReqCreateWargameDto dto, Long adminId) {
        // 관리자 유저 유효성 확인
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!"ADMIN".equals(admin.getRole())) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_VALID_ARGUMENT));

        Wargame wargame = Wargame.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .difficulty(dto.getDifficulty())
                .score(dto.getScore())
                .dockerImageUrl(dto.getDockerImageUrl())
                .fileUrl(dto.getFileUrl())
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        wargameRepository.save(wargame);

        WargameFlag flag = WargameFlag.builder()
                .wargame(wargame)
                .flag(dto.getFlag())
                .build();

        wargameFlagRepository.save(flag);

        return ResWargameListDto.fromEntity(wargame, false, false, false);
    }

    @Override
    @Transactional
    public void deleteWargame(Long wargameId) {
        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));
        wargameRepository.delete(wargame);
    }

    public List<ResWargameListDto> getWargameList(Long userId) {
        List<Wargame> wargames = wargameRepository.findAllWithCategory();
        List<Long> wargameIds = wargames.stream().map(Wargame::getId).toList();

        Set<Long> solvedIds;
        Set<Long> bookmarkedIds;
        Set<Long> likedIds;
        Map<Long, String> flagMap = new HashMap<>();

        if (userId != null) {
            solvedIds = solvedWargameRepository.findWargameIdsByUserId(userId);
            bookmarkedIds = bookmarkRepository.findWargameIdsByUserId(userId);
            likedIds = wargameLikeRepository.findWargameIdsByUserId(userId);
        } else {
            likedIds = new HashSet<>();
            bookmarkedIds = new HashSet<>();
            solvedIds = new HashSet<>();
        }

        // flag N+1 해결
        List<WargameFlag> flags = wargameFlagRepository.findAllByWargameIdIn(wargameIds);
        flagMap = flags.stream()
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

    @Override
    public ResWargameListDto getWargameById(Long userId, Long wargameId) {
        Wargame wargame = wargameRepository.findByIdWithCategory(wargameId)
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
        WargameFlag wargameFlag = wargameFlagRepository.findByWargame_Id(wargameId)
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

            badgeService.checkAndAssignBadges(user);
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

    @Override
    public List<ResHotWargameDto> getHotWargames() {
        Pageable top10 = PageRequest.of(0, 10);
        return wargameRepository.findHotWargames(top10).getContent();
    }

    @Override
    public List<ResUserStatusDto> getActiveUsersByWargame(Long wargameId) {
        String namespace = "ns-wargame";
        List<Pod> pods = kubernetesPodService.getRunningPodsByWargameId(wargameId, namespace);

        return pods.stream().map(pod -> {
            String podName = pod.getMetadata().getName();
            String[] parts = podName.split("-");

            Long userId;
            try {
                userId = Long.parseLong(parts[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                return null;
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return null;

            return new ResUserStatusDto(
                    user.getUsername(),
                    pod.getMetadata().getCreationTimestamp(),
                    false
            );
        }).filter(Objects::nonNull).toList();

    }

}
