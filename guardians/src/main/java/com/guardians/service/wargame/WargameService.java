package com.guardians.service.wargame;

import com.guardians.domain.wargame.entity.Difficulty;
import com.guardians.dto.wargame.res.*;

import java.util.List;

public interface WargameService {
    List<ResWargameListDto> getWargameList(Long userId);
    ResWargameListDto getWargameById(Long userId, Long wargameId);
    ResSubmitFlagDto submitFlag(Long userId, Long wargameId, String flag);
    List<ResHotWargameDto> getHotWargames();
    List<ResUserStatusDto> getActiveUsersByWargame(Long wargameId);
    ResWargameListDto createWargame(String title, String description, Difficulty difficulty, int score, Long categoryId, String dockerImageUrl, String fileUrl, String flag, Long adminId);
    void deleteWargame(Long wargameId);
    String getWargameFlag(Long wargameId);

    List<ResReviewListDto> getWargameReviews(Long wargameId);
    ResReviewListDto createReview(Long userId, Long wargameId, String content);
    ResReviewListDto updateReview(Long userId, Long reviewId, String content);
    void deleteReview(Long userId, Long reviewId);

    boolean toggleBookmark(Long userId, Long wargameId);
    boolean toggleLike(Long userId, Long wargameId);

}
