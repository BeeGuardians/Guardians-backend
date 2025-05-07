package com.guardians.service.wargame;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.wargame.entity.*;
import com.guardians.domain.wargame.repository.*;
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
    private final WargameLikeRepository wargameLikeRepository; // ✅ 추가

    @Override
    public List<ResWargameListDto> getWargameList(Long userId) {
        List<Wargame> wargames = wargameRepository.findAll();

        return wargames.stream().map(wargame -> {
            boolean solved = false;
            boolean bookmarked = false;
            boolean liked = false; // ✅ 선언

            if (userId != null) {
                solved = solvedWargameRepository.existsByUserIdAndWargameId(userId, wargame.getId());
                bookmarked = bookmarkRepository.existsByUserIdAndWargameId(userId, wargame.getId());
                liked = wargameLikeRepository.existsByUserIdAndWargameId(userId, wargame.getId()); // ✅ 수정
            }

            return ResWargameListDto.builder()
                    .id(wargame.getId())
                    .title(wargame.getTitle())
                    .description(wargame.getDescription())
                    .fileUrl(wargame.getFileUrl())
                    .category(wargame.getCategory().getId())
                    .difficulty(wargame.getDifficulty())
                    .createdAt(wargame.getCreatedAt())
                    .updatedAt(wargame.getUpdatedAt())
                    .solved(solved)
                    .bookmarked(bookmarked)
                    .liked(liked) // ✅ 추가
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResSubmitFlagDto submitFlag(Long userId, Long wargameId, String flag) {

        if (userId == null) {
            throw new CustomException(ErrorCode.NOT_LOGGED_IN);
        }
        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        WargameFlag wargameFlag = wargameFlagRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_VALID_ARGUMENT));

        boolean isCorrect = wargameFlag.getFlag().equals(flag);

        if (isCorrect && userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            boolean alreadySolved = solvedWargameRepository.existsByUserAndWargame(user, wargame);
            if (!alreadySolved) {
                SolvedWargame solved = SolvedWargame.builder()
                        .user(user)
                        .wargame(wargame)
                        .solvedAt(LocalDateTime.now())
                        .build();
                solvedWargameRepository.save(solved);
            }
        }

        return ResSubmitFlagDto.builder()
                .correct(isCorrect)
                .message(isCorrect ? "정답입니다!" : "틀렸습니다!")
                .build();
    }

    @Transactional
    @Override
    public boolean toggleBookmark(Long userId, Long wargameId) {

        if (userId == null) {
            throw new CustomException(ErrorCode.NOT_LOGGED_IN);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        Optional<Bookmark> bookmarkOpt = bookmarkRepository.findByUserAndWargame(user, wargame);

        if (bookmarkOpt.isPresent()) {
            bookmarkRepository.delete(bookmarkOpt.get());
            return false; // 북마크 해제됨
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .wargame(wargame)
                    .createdAt(LocalDateTime.now())
                    .build();
            bookmarkRepository.save(bookmark);
            return true; // 북마크 추가됨
        }
    }

    @Transactional
    @Override
    public boolean toggleLike(Long userId, Long wargameId) {

        if (userId == null) {
            throw new CustomException(ErrorCode.NOT_LOGGED_IN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Wargame wargame = wargameRepository.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        Optional<WargameLike> likeOpt = wargameLikeRepository.findByUserAndWargame(user, wargame);

        if (likeOpt.isPresent()) {
            wargameLikeRepository.delete(likeOpt.get());
            return false; // 좋아요 취소됨
        } else {
            WargameLike like = WargameLike.builder()
                    .user(user)
                    .wargame(wargame)
                    .createdAt(LocalDateTime.now())
                    .build();
            wargameLikeRepository.save(like);
            return true; // 좋아요 추가됨
        }
    }
}
