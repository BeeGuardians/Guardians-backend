package com.guardians.dto.job.req;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreateJobDto {
    private String companyName;
    private String title;
    private String description;
    private String location;
    private String employmentType;
    private String careerLevel;
    private String salary;
    private LocalDate deadline;
    private String sourceUrl;
}