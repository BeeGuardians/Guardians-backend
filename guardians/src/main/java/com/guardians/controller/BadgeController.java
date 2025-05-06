package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.service.badge.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/badges")
@RequiredArgsConstructor
@Tag(name = "Badge API", description = "뱃지 관련 API")
public class BadgeController {

    private final BadgeService badgeService;

    // 획득 뱃지 / 전체 뱃지 모두 조회
    @Operation(summary = "유저 뱃지 조회", description = "획득한 뱃지 여부 포함 전체 뱃지 목록 조회")
    @GetMapping
    public ResponseEntity<ResWrapper<?>> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ResWrapper.resSuccess("유저 뱃지 목록 조회 성공", badgeService.getAllBadgesWithUserStatus(userId))
        );
    }

    // 획득한 뱃지만 조회
    @Operation(summary = "획득한 뱃지만 조회", description = "유저가 획득한 뱃지만 반환")
    @GetMapping("/earned")
    public ResponseEntity<ResWrapper<?>> getEarnedBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ResWrapper.resSuccess("획득 뱃지 조회 성공", badgeService.getEarnedBadges(userId))
        );
    }

}
