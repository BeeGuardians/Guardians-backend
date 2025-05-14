package com.guardians.service.mypage;

import com.guardians.dto.mypage.res.*;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MypageService {

    ResProfileDto getProfile(Long userId);
    ResSolvedDto getSolvedProblems(Long userId);
    ResBookmarkDto getBookmarks(Long userId);
    ResPostDto getBoards(Long userId);
    ResReviewDto getReviews(Long userId);
    ResRankDto getRank(Long userId);
    List<ResRankDto> getAllRanks();
    ResUserStatsDto getUserStats(Long userId);

}
