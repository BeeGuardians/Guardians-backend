package com.guardians.dto.job.res;

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
}
