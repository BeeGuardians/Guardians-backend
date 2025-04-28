package com.guardians.service.mypage;

import com.guardians.dto.mypage.res.*;

public interface MypageService {

    ResProfileDto getProfile(Long userId);
    ResSolvedDto getSolvedProblems(Long userId);
    ResBookmarkDto getBookmarks(Long userId);
    ResPostDto getPosts(Long userId);
    ResReviewDto getReviews(Long userId);
    ResRankDto getRank(Long userId);
}
