package com.guardians.service.wargame;

import com.guardians.dto.mypage.res.ResReviewDto;
import com.guardians.dto.wargame.req.ReqCreateReviewDto;
import com.guardians.dto.wargame.req.ReqUpdateReviewDto;
import com.guardians.dto.wargame.res.ResReviewListDto;
import com.guardians.dto.wargame.res.ResSubmitFlagDto;
import com.guardians.dto.wargame.res.ResWargameListDto;

import java.util.List;

public interface WargameService {
    List<ResWargameListDto> getWargameList(Long userId);
    ResWargameListDto getWargameById(Long userId, Long wargameId);
    ResSubmitFlagDto submitFlag(Long userId, Long wargameId, String flag);

    List<ResReviewListDto> getWargameReviews(Long wargameId);
    ResReviewListDto createReview(Long userId, Long wargameId, ReqCreateReviewDto request);
    ResReviewListDto updateReview(Long userId, Long reviewId, ReqUpdateReviewDto request);
    void deleteReview(Long userId, Long reviewId);

    boolean toggleBookmark(Long userId, Long wargameId);
    boolean toggleLike(Long userId, Long wargameId);

}
