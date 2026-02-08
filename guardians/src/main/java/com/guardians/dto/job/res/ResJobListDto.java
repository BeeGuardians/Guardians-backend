package com.guardians.dto.job.res;

import com.guardians.domain.job.entity.Job;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ResJobListDto {
    private Long jobId;
    private String title;
    private String companyName;
    private String location;
    private String employmentType;
    private LocalDate deadline;
    private String careerLevel;
    private String sourceUrl;

    public static ResJobListDto fromEntity(Job job) {
        return ResJobListDto.builder()
                .jobId(job.getId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .deadline(job.getDeadline())
                .careerLevel(job.getCareerLevel())
                .sourceUrl(job.getSourceUrl())
                .build();
    }
}
