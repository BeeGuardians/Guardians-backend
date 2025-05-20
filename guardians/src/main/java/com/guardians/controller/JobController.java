package com.guardians.controller;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.job.req.ReqCreateJobDto;
import com.guardians.dto.job.req.ReqUpdateJobDto;
import com.guardians.dto.job.res.ResJobDto;
import com.guardians.dto.job.res.ResJobListDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import com.guardians.service.job.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
@Tag(name = "Job API", description = "채용정보 관련 API")
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    private static final String ADMIN_ROLE = "ADMIN";

    // ✅ 채용공고 등록
    @Operation(summary = "채용공고 등록", description = "관리자만 채용공고를 등록할 수 있습니다.")
    @PostMapping
    public ResponseEntity<ResWrapper<?>> createJob(
            @RequestBody @Valid ReqCreateJobDto dto,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        checkAdmin(userId);
        jobService.createJob(dto);
        return ResponseEntity.ok(ResWrapper.resSuccess("채용공고 등록 완료", null));
    }

    // ✅ 채용공고 수정
    @Operation(summary = "채용공고 수정", description = "관리자만 채용공고를 수정할 수 있습니다.")
    @PatchMapping("/{jobId}")
    public ResponseEntity<ResWrapper<?>> updateJob(
            @PathVariable Long jobId,
            @RequestBody @Valid ReqUpdateJobDto dto,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        checkAdmin(userId);
        jobService.updateJob(jobId, dto);
        return ResponseEntity.ok(ResWrapper.resSuccess("채용공고 수정 완료", null));
    }
    // ✅ 채용공고 삭제
    @Operation(summary = "채용공고 삭제", description = "관리자만 채용공고를 삭제할 수 있습니다.")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ResWrapper<?>> deleteJob(
            @PathVariable Long jobId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        checkAdmin(userId);
        jobService.deleteJob(jobId);
        return ResponseEntity.ok(ResWrapper.resSuccess("채용공고 삭제 완료", null));
    }

    // ✅ 채용공고 목록 조회 (전체 공개)
    @Operation(summary = "채용공고 목록 조회", description = "모든 공개 채용공고를 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ResWrapper<?>> getJobList() {
        List<ResJobListDto> result = jobService.getJobList();
        return ResponseEntity.ok(ResWrapper.resSuccess("채용공고 목록 조회 성공", result));
    }

    // ✅ 채용공고 상세 조회 (전체 공개)
    @Operation(summary = "채용공고 상세 조회", description = "특정 채용공고의 상세 정보를 조회합니다.")
    @GetMapping("/{jobId}")
    public ResponseEntity<ResWrapper<?>> getJobDetail(@PathVariable Long jobId) {
        ResJobDto result = jobService.getJobDetail(jobId);
        return ResponseEntity.ok(ResWrapper.resSuccess("채용공고 상세 조회 성공", result));
    }

    private void checkAdmin(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.NOT_LOGGED_IN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!"ADMIN".equals(user.getRole())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

}