package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.application.ranking.RankingFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
@Tag(name = "Ranking API", description = "전체 랭킹 조회 관련 API")
public class RankingController {

    private final RankingFacade rankingFacade;

    @Operation(summary = "전체 랭킹 조회", description = "모든 사용자들의 점수 순 정렬")
    @GetMapping
    public ResponseEntity<ResWrapper<?>> getAllRanks() {
        return ResponseEntity.ok(
                ResWrapper.resSuccess("전체 랭킹 조회 성공", rankingFacade.getAllRanks())
        );
    }
}
