package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.mypage.res.ResSolvedDto;
import com.guardians.service.mypage.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}")
@RequiredArgsConstructor
@Tag(name = "Mypage API", description = "마이페이지 조회 관련 API")
public class MypageController {

    private final MypageService mypageService;

    @Operation(summary = "마이페이지 프로필 조회", description = "닉네임, 이메일, 가입일자 같은 기본 정보 조회")
    @GetMapping
    public ResponseEntity<ResWrapper<?>> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(ResWrapper.resSuccess("마이페이지 조회 성공", mypageService.getProfile(userId)));
    }

    @Operation(summary = "푼 문제 리스트 조회", description = "내가 푼 문제 목록")
    @GetMapping("/solved")
    public ResponseEntity<ResWrapper<?>> getSolvedProblems(@PathVariable Long userId) {
        ResSolvedDto solvedDto = mypageService.getSolvedProblems(userId);
        int count = solvedDto.getSolvedList().size();
        return ResponseEntity.ok(ResWrapper.resList("푼 문제 조회 성공", solvedDto.getSolvedList(), count));
    }

    @Operation(summary = "북마크한 문제 리스트 조회", description = "내가 북마크한 문제 목록")
    @GetMapping("/bookmarks")
    public ResponseEntity<ResWrapper<?>> getBookmarks(@PathVariable Long userId) {
        var dto = mypageService.getBookmarks(userId); // ResBookmarkDto
        int count = dto.getBookmarks().size();
        return ResponseEntity.ok(ResWrapper.resList("북마크 조회 성공", dto.getBookmarks(), count));
    }

    @Operation(summary = "작성한 게시글 리스트 조회", description = "내가 작성한 자유게시판 글 목록")
    @GetMapping("/boards")
    public ResponseEntity<ResWrapper<?>> getPosts(@PathVariable Long userId) {
        var dto = mypageService.getPosts(userId); // 예: ResBoardDto
        int count = dto.getPosts().size(); // 또는 getPosts()일 수도 있음
        return ResponseEntity.ok(ResWrapper.resList("내 게시글 조회 성공", dto.getPosts(), count));
    }

    @Operation(summary = "작성한 리뷰 리스트 조회", description = "내가 작성한 워게임 리뷰 목록")
    @GetMapping("/reviews")
    public ResponseEntity<ResWrapper<?>> getReviews(@PathVariable Long userId) {
        var dto = mypageService.getReviews(userId); // ResReviewDto
        int count = dto.getReviews().size();
        return ResponseEntity.ok(ResWrapper.resList("내 리뷰 조회 성공", dto.getReviews(), count));
    }

    @Operation(summary = "내 랭킹 조회", description = "현재 내 랭킹 정보 조회")
    @GetMapping("/rank")
    public ResponseEntity<ResWrapper<?>> getRank(@PathVariable Long userId) {
        return ResponseEntity.ok(ResWrapper.resSuccess("내 랭킹 조회 성공", mypageService.getRank(userId)));
    }

    @Operation(summary = "내 통계 정보 조회", description = "점수, 랭킹, 푼 문제 수 조회")
    @GetMapping("/stats")
    public ResponseEntity<ResWrapper<?>> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ResWrapper.resSuccess("내 통계 정보 조회 성공", mypageService.getUserStats(userId))
        );
    }

}
