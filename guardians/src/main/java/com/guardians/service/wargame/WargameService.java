package com.guardians.service.wargame;

import com.guardians.dto.wargame.req.ReqCreateReviewDto;
import com.guardians.dto.wargame.req.ReqCreateWargameDto;
import com.guardians.dto.wargame.req.ReqUpdateReviewDto;
import com.guardians.dto.wargame.res.*;

import java.util.List;

public interface WargameService {
    List<ResWargameListDto> getWargameList(Long userId);
    ResWargameListDto getWargameById(Long userId, Long wargameId);
    ResSubmitFlagDto submitFlag(Long userId, Long wargameId, String flag);
    List<ResHotWargameDto> getHotWargames();
    List<ResUserStatusDto> getActiveUsersByWargame(Long wargameId);
    ResWargameListDto createWargame(ReqCreateWargameDto dto, Long adminId);
    void deleteWargame(Long wargameId);
    String getWargameFlag(Long wargameId);

    List<ResReviewListDto> getWargameReviews(Long wargameId);
    ResReviewListDto createReview(Long userId, Long wargameId, ReqCreateReviewDto request);
    ResReviewListDto updateReview(Long userId, Long reviewId, ReqUpdateReviewDto request);
    void deleteReview(Long userId, Long reviewId);

    boolean toggleBookmark(Long userId, Long wargameId);
    boolean toggleLike(Long userId, Long wargameId);

}
