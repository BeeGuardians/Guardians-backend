package com.guardians.application.job;

import com.guardians.domain.job.entity.Job;
import com.guardians.domain.job.port.JobPort;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.port.UserPort;
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
public class JobFacade {

    private final JobPort jobPort;
    private final UserPort userPort;

    public void verifyAdmin(Long userId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.isAdmin()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Transactional
    public void createJob(String companyName, String title, String description, String location,
                          String employmentType, String careerLevel, String salary,
                          LocalDate deadline, String sourceUrl) {
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

        jobPort.save(job);
    }

    public ResJobDto getJobDetail(Long jobId) {
        Job job = jobPort.findByIdAndIsActiveTrue(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        return ResJobDto.fromEntity(job);
    }

    public List<ResJobListDto> getJobList() {
        return jobPort.findByIsActiveTrueAndDeadlineAfter(LocalDate.now())
                .stream()
                .map(ResJobListDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateJob(Long jobId, String title, String description, String salary,
                          LocalDate deadline, Boolean isActive) {
        Job job = jobPort.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        job.update(title, description, salary, deadline, isActive);
    }

    @Transactional
    public void deleteJob(Long jobId) {
        Job job = jobPort.findById(jobId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_NOT_FOUND));
        jobPort.delete(job);
    }
}
