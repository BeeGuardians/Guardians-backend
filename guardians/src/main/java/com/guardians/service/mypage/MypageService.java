package com.guardians.service.mypage;

import com.guardians.dto.mypage.res.*;

import java.util.List;

public interface MypageService {

    ResProfileDto getProfile(Long userId);
    ResSolvedDto getSolvedProblems(Long userId);
    ResBookmarkDto getBookmarks(Long userId);
    ResPostDto getPosts(Long userId);
    ResReviewDto getReviews(Long userId);
    ResRankDto getRank(Long userId);

    // ✅ 전체 랭킹 조회 추가
    List<ResRankDto> getAllRanks();
}
