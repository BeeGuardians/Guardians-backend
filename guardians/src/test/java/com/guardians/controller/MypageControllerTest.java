package com.guardians.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.dto.mypage.res.*;
import com.guardians.service.mypage.MypageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // ✅ 추가
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MypageController.class)
@WithMockUser
class MypageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MypageService mypageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("마이페이지 프로필 조회 성공")
    void testGetProfile() throws Exception {
        ResProfileDto profileDto = ResProfileDto.builder()
                .userId(1L)
                .username("junhyeong")
                .email("test@example.com")
                .profileImageUrl("http://example.com/image.jpg")
                .build();

        when(mypageService.getProfile(anyLong())).thenReturn(profileDto);

        mockMvc.perform(get("/api/users/1"))
                .andDo(print()) // 👈 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("마이페이지 조회 성공"))
                .andExpect(jsonPath("$.result.data.username").value("junhyeong"));
    }

    @Test
    @DisplayName("푼 문제 리스트 조회 성공")
    void testGetSolvedProblems() throws Exception {
        ResSolvedDto.SolvedInfo solved = ResSolvedDto.SolvedInfo.builder()
                .wargameId(1L)
                .title("Basic Exploit")
                .category("Binary")
                .score(100)
                .solvedAt(LocalDateTime.parse("2025-06-01T10:00:00"))
                .build();

        ResSolvedDto dto = ResSolvedDto.builder()
                .solvedList(List.of(solved))
                .build();

        when(mypageService.getSolvedProblems(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/api/users/1/solved"))
                .andDo(print()) // 👈 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("푼 문제 조회 성공"))
                .andExpect(jsonPath("$.result.count").value(1))
                .andExpect(jsonPath("$.result.data.solvedList[0].title").value("Basic Exploit"));
    }

    @Test
    @DisplayName("북마크한 문제 리스트 조회 성공")
    void testGetBookmarks() throws Exception {
        ResBookmarkDto.BookmarkInfo bookmark = ResBookmarkDto.BookmarkInfo.builder()
                .wargameId(1L)
                .title("Heap Exploit")
                .build();

        ResBookmarkDto dto = ResBookmarkDto.builder()
                .bookmarks(List.of(bookmark))
                .build();

        when(mypageService.getBookmarks(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/api/users/1/bookmarks"))
                .andDo(print()) // 👈 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("북마크 조회 성공"))
                .andExpect(jsonPath("$.result.count").value(1))
                .andExpect(jsonPath("$.result.data.bookmarks[0].title").value("Heap Exploit"));
    }

    @Test
    @DisplayName("작성한 게시글 리스트 조회 성공")
    void testGetBoards() throws Exception {
        ResBoardDto.BoardInfo board = ResBoardDto.BoardInfo.builder()
                .boardId(10L)
                .title("CTF 문제 질문")
                .content("이 문제에서 왜 쉘이 안 떠요?")
                .boardType("FREE")
                .createdAt("2025-06-01T12:00:00")
                .likeCount(7)
                .build();

        ResBoardDto dto = ResBoardDto.builder()
                .boards(List.of(board))
                .build();

        when(mypageService.getBoards(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/api/users/1/boards"))
                .andDo(print()) // 👈 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("내 게시글 조회 성공"))
                .andExpect(jsonPath("$.result.count").value(1))
                .andExpect(jsonPath("$.result.data.boards[0].title").value("CTF 문제 질문"));
    }

    @Test
    @DisplayName("작성한 리뷰 리스트 조회 성공")
    void testGetReviews() throws Exception {
        ResReviewDto.ReviewDto review = ResReviewDto.ReviewDto.builder()
                .id(1L)
                .content("이 문제 정말 재밌었어요!")
                .wargameTitle("Stack Overflow 101")
                .createdAt("2025-06-01T10:00:00")
                .build();

        ResReviewDto dto = ResReviewDto.builder()
                .reviews(List.of(review))
                .build();

        when(mypageService.getReviews(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/api/users/1/reviews"))
                .andDo(print()) // 👈 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("내 리뷰 조회 성공"))
                .andExpect(jsonPath("$.result.count").value(1))
                .andExpect(jsonPath("$.result.data.reviews[0].wargameTitle").value("Stack Overflow 101"));
    }

    @Test
    @DisplayName("내 랭킹 조회 성공")
    void testGetRank() throws Exception {
        ResRankDto dto = ResRankDto.builder()
                .userId(1L)
                .username("junhyeong")
                .score(1500)
                .totalSolved(50)
                .rank(3)
                .userProfileUrl("http://example.com/profile.jpg")
                .build();

        when(mypageService.getRank(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/api/users/1/rank"))
                .andDo(print()) // 👈 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("내 랭킹 조회 성공"))
                .andExpect(jsonPath("$.result.data.rank").value(3))
                .andExpect(jsonPath("$.result.data.username").value("junhyeong"));
    }

    @Test
    @DisplayName("내 통계 정보 조회 성공")
    void testGetUserStats() throws Exception {
        ResUserStatsDto statsDto = ResUserStatsDto.builder()
                .score(1200)
                .rank(4)
                .tier("SILVER")
                .solvedCount(25)
                .build();

        when(mypageService.getUserStats(anyLong())).thenReturn(statsDto);

        mockMvc.perform(get("/api/users/1/stats"))
                .andDo(print()) // 👈 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("내 통계 정보 조회 성공"))
                .andExpect(jsonPath("$.result.data.rank").value(4))
                .andExpect(jsonPath("$.result.data.tier").value("SILVER"))
                .andExpect(jsonPath("$.result.data.score").value(1200))
                .andExpect(jsonPath("$.result.data.solvedCount").value(25));
    }
}
