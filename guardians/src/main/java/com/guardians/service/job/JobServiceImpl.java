package com.guardians.service.job;

import com.guardians.domain.job.entity.Job;
import com.guardians.domain.job.repository.JobRepository;
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
@Transactional(readOnly = true)
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    @Transactional
    public void createJob(String companyName, String title, String description, String location, String employmentType, String careerLevel, String salary, LocalDate deadline, String sourceUrl) {
        Job job = Job.builder()
                .companyName(companyName)
                .title(title)
                .description(description)
                .location(location)
                .employmentType(employmentType)
                .careerLevel(careerLevel)
                .salary(salary)
                .deadline(deadline)
                .sourceUrl(sourceUrl)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public ResJobDto getJobDetail(Long jobId) {
        Job job = jobRepository.findByIdAndIsActiveTrue(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        return ResJobDto.fromEntity(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResJobListDto> getJobList() {
        return jobRepository.findByIsActiveTrueAndDeadlineAfter(LocalDate.now())
                .stream()
                .map(ResJobListDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateJob(Long jobId, String title, String description, String salary, LocalDate deadline, Boolean isActive) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));

        job.update(title, description, salary, deadline, isActive);
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        jobRepository.delete(job);
    }
}
