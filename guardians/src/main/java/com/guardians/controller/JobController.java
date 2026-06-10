package com.guardians.controller;

import com.guardians.application.job.JobFacade;
import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.job.req.ReqCreateJobDto;
import com.guardians.dto.job.req.ReqUpdateJobDto;
import com.guardians.dto.job.res.ResJobDto;
import com.guardians.dto.job.res.ResJobListDto;
import com.guardians.util.SessionUtil;
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

    private final JobFacade jobFacade;

    @Operation(summary = "채용공고 등록", description = "관리자만 채용공고를 등록할 수 있습니다.")
    @PostMapping
    public ResponseEntity<ResWrapper<?>> createJob(
            @RequestBody @Valid ReqCreateJobDto dto,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        jobFacade.verifyAdmin(userId);
        jobFacade.createJob(dto.getCompanyName(), dto.getTitle(), dto.getDescription(), dto.getLocation(), dto.getEmploymentType(), dto.getCareerLevel(), dto.getSalary(), dto.getDeadline(), dto.getSourceUrl());
        return ResponseEntity.ok(ResWrapper.resSuccess("[관리자] 채용공고 등록 완료", null));
    }

    @Operation(summary = "채용공고 수정", description = "관리자만 채용공고를 수정할 수 있습니다.")
    @PatchMapping("/{jobId}")
    public ResponseEntity<ResWrapper<?>> updateJob(
            @PathVariable Long jobId,
            @RequestBody @Valid ReqUpdateJobDto dto,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        jobFacade.verifyAdmin(userId);
        jobFacade.updateJob(jobId, dto.getTitle(), dto.getDescription(), dto.getSalary(), dto.getDeadline(), dto.getIsActive());
        return ResponseEntity.ok(ResWrapper.resSuccess("[관리자] 채용공고 수정 완료", null));
    }

    @Operation(summary = "채용공고 삭제", description = "관리자만 채용공고를 삭제할 수 있습니다.")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ResWrapper<?>> deleteJob(
            @PathVariable Long jobId,
            HttpSession session
    ) {
        Long userId = SessionUtil.getRequiredUserId(session);
        jobFacade.verifyAdmin(userId);
        jobFacade.deleteJob(jobId);
        return ResponseEntity.ok(ResWrapper.resSuccess("[관리자] 채용공고 삭제 완료", null));
    }

    @Operation(summary = "채용공고 목록 조회", description = "모든 공개 채용공고를 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ResWrapper<?>> getJobList() {
        List<ResJobListDto> result = jobFacade.getJobList();
        return ResponseEntity.ok(ResWrapper.resList("채용공고 목록 조회 성공", result, result.size()));
    }

    @Operation(summary = "채용공고 상세 조회", description = "특정 채용공고의 상세 정보를 조회합니다.")
    @GetMapping("/{jobId}")
    public ResponseEntity<ResWrapper<?>> getJobDetail(@PathVariable Long jobId) {
        ResJobDto result = jobFacade.getJobDetail(jobId);
        return ResponseEntity.ok(ResWrapper.resSuccess("채용공고 상세 조회 성공", result));
    }
}
