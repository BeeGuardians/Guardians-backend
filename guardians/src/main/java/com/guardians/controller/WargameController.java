package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.wargame.req.ReqCreateReviewDto;
import com.guardians.dto.wargame.req.ReqSubmitFlagDto;
import com.guardians.dto.wargame.req.ReqUpdateReviewDto;
import com.guardians.dto.wargame.res.ResReviewListDto;
import com.guardians.dto.wargame.res.ResSubmitFlagDto;
import com.guardians.dto.wargame.res.ResWargameListDto;
import com.guardians.service.wargame.KubernetesPodService;
import com.guardians.service.wargame.WargameService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wargames")
public class WargameController {

    private final WargameService wargameService;
    private final KubernetesPodService kubernetesPodService;

    @GetMapping
    public ResponseEntity<ResWrapper<?>> getWargameList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userId = (session != null) ? (Long) session.getAttribute("userId") : null;

        List<ResWargameListDto> result = wargameService.getWargameList(userId);
        return ResponseEntity.ok(ResWrapper.resList("워게임 목록 조회 성공", result, result.size()));
    }

    @GetMapping("/{wargameId}")
    public ResponseEntity<ResWrapper<?>> getWargameById(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        ResWargameListDto result = wargameService.getWargameById(userId, wargameId);
        return ResponseEntity.ok(ResWrapper.resSuccess("워게임 상세 조회 성공", result));
    }


    @PostMapping("/{wargameId}/submit")
    public ResponseEntity<ResWrapper<?>> submitFlag(
            @PathVariable Long wargameId,
            @RequestBody ReqSubmitFlagDto request,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        ResSubmitFlagDto result = wargameService.submitFlag(userId, wargameId, request.getFlag());
        return ResponseEntity.ok(ResWrapper.resSuccess("채점 완료", result));
    }

    @PostMapping("/{wargameId}/bookmark")
    public ResponseEntity<ResWrapper<?>> toggleBookmark(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        boolean bookmarked = wargameService.toggleBookmark(userId, wargameId);
        return ResponseEntity.ok(ResWrapper.resSuccess("북마크 토글 완료", Map.of("bookmarked", bookmarked)));
    }

    @PostMapping("/{wargameId}/like")
    public ResponseEntity<ResWrapper<?>> toggleLike(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        boolean liked = wargameService.toggleLike(userId, wargameId);
        return ResponseEntity.ok(ResWrapper.resSuccess("좋아요 토글 완료", Map.of("liked", liked)));
    }

    @GetMapping("/{wargameId}/reviews")
    public ResponseEntity<ResWrapper<?>> getWargameReviews(
            @PathVariable Long wargameId
    ) {
        List<ResReviewListDto> reviews = wargameService.getWargameReviews(wargameId);
        return ResponseEntity.ok(ResWrapper.resList("워게임 리뷰 조회 성공", reviews, reviews.size()));
    }

    @PostMapping("/{wargameId}/reviews")
    public ResponseEntity<ResWrapper<?>> createReview(
            @PathVariable Long wargameId,
            @RequestBody ReqCreateReviewDto request,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        ResReviewListDto result = wargameService.createReview(userId, wargameId, request);
        return ResponseEntity.ok(ResWrapper.resSuccess("리뷰 작성 성공", result));
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ResWrapper<?>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReqUpdateReviewDto request,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        ResReviewListDto result = wargameService.updateReview(userId, reviewId, request);
        return ResponseEntity.ok(ResWrapper.resSuccess("리뷰 수정 성공", result));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ResWrapper<?>> deleteReview(
            @PathVariable Long reviewId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        wargameService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(ResWrapper.resSuccess("리뷰 삭제 성공", null));
    }


    @PostMapping("/{wargameId}/start")
    public ResponseEntity<ResWrapper<?>> startWargamePod(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ResWrapper.resException(new Exception("로그인이 필요합니다.")));
        }

        String podName = "wargame-" + userId + "-" + wargameId;
        String namespace = "default";

        kubernetesPodService.createWargamePod(podName, wargameId, userId, namespace);
        String url = "http://" + podName + ".example.com";

        return ResponseEntity.ok(
                ResWrapper.resSuccess("워게임 인스턴스 시작됨", Map.of(
                        "podName", podName,
                        "url", url
                ))
        );
    }

    @DeleteMapping("/{wargameId}/stop")
    public ResponseEntity<ResWrapper<?>> stopWargamePod(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(ResWrapper.resException(new Exception("로그인이 필요합니다.")));
        }

        String podName = "wargame-" + userId + "-" + wargameId;
        String namespace = "default";

        boolean deleted = kubernetesPodService.deleteWargamePod(podName, namespace);
        if (deleted) {
            return ResponseEntity.ok(ResWrapper.resSuccess("워게임 인스턴스 종료됨", null));
        } else {
            return ResponseEntity.ok(ResWrapper.resSuccess("종료할 워게임 인스턴스를 찾을 수 없음", null));
        }
    }


}
