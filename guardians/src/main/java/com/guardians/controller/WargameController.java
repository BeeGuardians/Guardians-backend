package com.guardians.controller;

import com.guardians.application.wargame.WargameFacade;
import com.guardians.domain.wargame.port.KubernetesPodPort;
import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.wargame.req.ReqCreateReviewDto;
import com.guardians.dto.wargame.req.ReqCreateWargameDto;
import com.guardians.dto.wargame.req.ReqSubmitFlagDto;
import com.guardians.dto.wargame.req.ReqUpdateReviewDto;
import com.guardians.dto.wargame.res.*;
import com.guardians.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wargames")
public class WargameController {

    private final WargameFacade wargameFacade;
    private final KubernetesPodPort kubernetesPodPort;

    @PostMapping("/admin")
    public ResponseEntity<ResWrapper<?>> createWargame(
            @RequestBody @Valid ReqCreateWargameDto request,
            HttpSession session
    ) {
        Long userId = SessionUtil.requireAdmin(session);
        ResWargameListDto created = wargameFacade.createWargame(request.getTitle(), request.getDescription(), request.getDifficulty(), request.getScore(), request.getCategoryId(), request.getDockerImageUrl(), request.getFileUrl(), request.getFlag(), userId);
        return ResponseEntity.ok(ResWrapper.resSuccess("[관리자] 워게임 생성 성공", created));
    }

    @DeleteMapping("/admin/{wargameId}")
    public ResponseEntity<ResWrapper<?>> deleteWargame(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        SessionUtil.requireAdmin(session);
        wargameFacade.deleteWargame(wargameId);
        return ResponseEntity.ok(ResWrapper.resSuccess("워게임 삭제 완료", null));
    }


    @GetMapping
    public ResponseEntity<ResWrapper<?>> getWargameList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userId = SessionUtil.getUserIdOrNull(session);

        List<ResWargameListDto> result = wargameFacade.getWargameList(userId);
        return ResponseEntity.ok(ResWrapper.resList("워게임 목록 조회 성공", result, result.size()));
    }

    @GetMapping("/{wargameId}")
    public ResponseEntity<ResWrapper<?>> getWargameById(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = SessionUtil.getUserIdOrNull(session);
        ResWargameListDto result = wargameFacade.getWargameById(userId, wargameId);
        return ResponseEntity.ok(ResWrapper.resSuccess("워게임 상세 조회 성공", result));
    }


    @PostMapping("/{wargameId}/submit")
    public ResponseEntity<ResWrapper<?>> submitFlag(
            @PathVariable Long wargameId,
            @RequestBody @Valid ReqSubmitFlagDto request,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        ResSubmitFlagDto result = wargameFacade.submitFlag(userId, wargameId, request.getFlag());
        return ResponseEntity.ok(ResWrapper.resSuccess("채점 완료", result));
    }

    @PostMapping("/{wargameId}/bookmark")
    public ResponseEntity<ResWrapper<?>> toggleBookmark(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        boolean bookmarked = wargameFacade.toggleBookmark(userId, wargameId);
        return ResponseEntity.ok(ResWrapper.resSuccess("북마크 토글 완료", Map.of("bookmarked", bookmarked)));
    }

    @PostMapping("/{wargameId}/like")
    public ResponseEntity<ResWrapper<?>> toggleLike(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        boolean liked = wargameFacade.toggleLike(userId, wargameId);
        return ResponseEntity.ok(ResWrapper.resSuccess("좋아요 토글 완료", Map.of("liked", liked)));
    }

    @GetMapping("/{wargameId}/reviews")
    public ResponseEntity<ResWrapper<?>> getWargameReviews(
            @PathVariable Long wargameId
    ) {
        List<ResReviewListDto> reviews = wargameFacade.getWargameReviews(wargameId);
        return ResponseEntity.ok(ResWrapper.resList("워게임 리뷰 조회 성공", reviews, reviews.size()));
    }

    @PostMapping("/{wargameId}/reviews")
    public ResponseEntity<ResWrapper<?>> createReview(
            @PathVariable Long wargameId,
            @RequestBody @Valid ReqCreateReviewDto request,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        ResReviewListDto result = wargameFacade.createReview(userId, wargameId, request.getContent());
        return ResponseEntity.ok(ResWrapper.resSuccess("리뷰 작성 성공", result));
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ResWrapper<?>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReqUpdateReviewDto request,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        ResReviewListDto result = wargameFacade.updateReview(userId, reviewId, request.getContent());
        return ResponseEntity.ok(ResWrapper.resSuccess("리뷰 수정 성공", result));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ResWrapper<?>> deleteReview(
            @PathVariable Long reviewId,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        wargameFacade.deleteReview(userId, reviewId);
        return ResponseEntity.ok(ResWrapper.resSuccess("리뷰 삭제 성공", null));
    }


    @PostMapping("/{wargameId}/start")
    public ResponseEntity<ResWrapper<?>> startWargamePod(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);

        String podName = "wargame-" + userId + "-" + wargameId;
        String namespace = "ns-wargame";

        kubernetesPodPort.createWargamePod(podName, wargameId, userId, namespace);

        String url = String.format("https://%d-%d.wargames.bee-guardians.com", wargameId, userId);

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
        Long userId = SessionUtil.getRequiredUserId(session);

        String podName = "wargame-" + userId + "-" + wargameId;
        String namespace = "ns-wargame";

        boolean deleted = kubernetesPodPort.deleteWargamePod(podName, namespace);
        if (deleted) {
            return ResponseEntity.ok(
                    ResWrapper.resSuccess("워게임 인스턴스 종료됨", Map.of(
                            "podName", podName,
                            "url", kubernetesPodPort.generateIngressUrl(podName)
                    ))
            );
        } else {
            return ResponseEntity.ok(
                    ResWrapper.resSuccess("종료할 워게임 인스턴스를 찾을 수 없음", Map.of(
                            "podName", podName
                    ))
            );
        }
    }

    @GetMapping("/{wargameId}/status")
    public ResponseEntity<ResWrapper<?>> getPodStatus(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);

        String podName = "wargame-" + userId + "-" + wargameId;
        String namespace = "ns-wargame";

        PodStatusDto podStatus = kubernetesPodPort.getPodStatus(podName, namespace);

        Map<String, Object> result = new HashMap<>();
        result.put("status", podStatus.getStatus());
        result.put("url", podStatus.getUrl());

        return ResponseEntity.ok(ResWrapper.resSuccess("Pod 상태 조회 성공", result));
    }

    @GetMapping("/hot")
    public ResponseEntity<ResWrapper<?>> getHotWargames() {
        List<ResHotWargameDto> hotWargames = wargameFacade.getHotWargames();
        return ResponseEntity.ok(
                ResWrapper.resList("지금 핫한 워게임 TOP 10", hotWargames, hotWargames.size())
        );
    }

    @GetMapping("/{wargameId}/active-users/list")
    public ResponseEntity<ResWrapper<?>> getActiveUserList(@PathVariable Long wargameId) {
        List<ResUserStatusDto> users = wargameFacade.getActiveUsersByWargame(wargameId);
        return ResponseEntity.ok(ResWrapper.resList("현재 워게임 풀고 있는 유저 목록", users, users.size()));
    }

    @PostMapping("/kali/start")
    public ResponseEntity<ResWrapper<?>> startKaliPod(HttpSession session) {
        Long userId = SessionUtil.getRequiredUserId(session);

        String namespace = "ns-wargame";
        kubernetesPodPort.createKaliPod(userId, namespace);

        String url = String.format("https://kali-%d.wargames.bee-guardians.com", userId);
        return ResponseEntity.ok(
                ResWrapper.resSuccess("Kali 인스턴스 시작됨", Map.of(
                        "url", url
                ))
        );
    }

    @DeleteMapping("/kali/stop")
    public ResponseEntity<ResWrapper<?>> stopKaliPod(HttpSession session) {
        Long userId = SessionUtil.getRequiredUserId(session);

        String namespace = "ns-wargame";
        boolean deleted = kubernetesPodPort.deleteKaliPod(userId, namespace);
        return ResponseEntity.ok(
                ResWrapper.resSuccess(deleted ? "Kali 인스턴스 종료됨" : "Kali 인스턴스 삭제 실패", null)
        );
    }

    @GetMapping("/kali/status")
    public ResponseEntity<ResWrapper<?>> getKaliPodStatus(HttpSession session) {
        Long userId = SessionUtil.getRequiredUserId(session);

        String namespace = "ns-wargame";
        PodStatusDto status = kubernetesPodPort.getKaliPodStatus(userId, namespace);
        return ResponseEntity.ok(
                ResWrapper.resSuccess("Kali 상태 조회 성공", Map.of(
                        "status", Optional.ofNullable(status.getStatus()).orElse("UNKNOWN"),
                        "url", Optional.ofNullable(status.getUrl()).orElse("")
                ))
        );
    }

    @GetMapping("/admin/{wargameId}/flag")
    public ResponseEntity<ResWrapper<?>> getWargameFlagForAdmin(
            @PathVariable Long wargameId,
            HttpSession session
    ) {
        SessionUtil.requireAdmin(session);

        String flag = wargameFacade.getWargameFlag(wargameId);

        return ResponseEntity.ok(ResWrapper.resSuccess("워게임 플래그 조회 성공", Map.of("flag", flag)));
    }

}
