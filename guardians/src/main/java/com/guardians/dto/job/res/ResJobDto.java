package com.guardians.dto.job.res;

import com.guardians.domain.job.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ResJobDto {
    private final Long id;
    private final String companyName;
    private final String title;
    private final String description;
    private final String location;
    private final String employmentType;
    private final String careerLevel;
    private final String salary;
    private final LocalDate deadline;

    public ResJobDto(Job job) {
        this.id = job.getId();
        this.companyName = job.getCompanyName();
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.location = job.getLocation();
        this.employmentType = job.getEmploymentType();
        this.careerLevel = job.getCareerLevel();
        this.salary = job.getSalary();
        this.deadline = job.getDeadline();
    }

    public static ResJobDto fromEntity(Job job) {
        return ResJobDto.builder()
                .id(job.getId())
                .companyName(job.getCompanyName())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .careerLevel(job.getCareerLevel())
                .salary(job.getSalary())
                .deadline(job.getDeadline())
                .build();
    }
}