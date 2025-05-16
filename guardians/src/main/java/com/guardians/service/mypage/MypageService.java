package com.guardians.service.mypage;

import com.guardians.dto.mypage.res.*;

import java.util.List;

public interface MypageService {

    ResProfileDto getProfile(Long userId);
    ResSolvedDto getSolvedProblems(Long userId);
    ResBookmarkDto getBookmarks(Long userId);
    ResBoardDto getBoards(Long userId);
    ResReviewDto getReviews(Long userId);
    ResRankDto getRank(Long userId);
    List<ResRankDto> getAllRanks();
    ResUserStatsDto getUserStats(Long userId);

}
