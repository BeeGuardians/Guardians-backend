package com.guardians.service.job;

import com.guardians.domain.job.entity.Job;
import com.guardians.domain.job.repository.JobRepository;
import com.guardians.dto.job.req.ReqCreateJobDto;
import com.guardians.dto.job.req.ReqUpdateJobDto;
import com.guardians.dto.job.res.ResJobDto;
import com.guardians.dto.job.res.ResJobListDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    @Transactional
    public void createJob(ReqCreateJobDto dto) {
        Job job = new Job();
        job.setCompanyName(dto.getCompanyName());
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setEmploymentType(dto.getEmploymentType());
        job.setCareerLevel(dto.getCareerLevel());
        job.setSalary(dto.getSalary());
        job.setDeadline(dto.getDeadline());
        job.setSourceUrl(dto.getSourceUrl());
        job.setIsActive(true);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public ResJobDto getJobDetail(Long jobId) {
        Job job = jobRepository.findByIdAndIsActiveTrue(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        return new ResJobDto(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResJobListDto> getJobList() {
        return jobRepository.findByIsActiveTrueAndDeadlineAfter(LocalDate.now())
                .stream()
                .map(job -> ResJobListDto.builder()
                        .jobId(job.getId())
                        .title(job.getTitle())
                        .companyName(job.getCompanyName())
                        .location(job.getLocation())
                        .employmentType(job.getEmploymentType())
                        .deadline(job.getDeadline())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateJob(Long jobId, ReqUpdateJobDto dto) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSalary(dto.getSalary());
        job.setDeadline(dto.getDeadline());
        job.setIsActive(dto.getIsActive());
        job.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        jobRepository.delete(job);
    }
}
