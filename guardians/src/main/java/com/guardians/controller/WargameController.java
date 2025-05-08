package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.wargame.req.ReqSubmitFlagDto;
import com.guardians.dto.wargame.res.ResSubmitFlagDto;
import com.guardians.dto.wargame.res.ResWargameListDto;
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

    @GetMapping
    public ResponseEntity<ResWrapper<?>> getWargameList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userId = (session != null) ? (Long) session.getAttribute("userId") : null;

        List<ResWargameListDto> result = wargameService.getWargameList(userId);
        return ResponseEntity.ok(ResWrapper.resSuccess("워게임 목록 조회 성공", result));
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

}
