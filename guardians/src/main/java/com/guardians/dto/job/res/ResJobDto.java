package com.guardians.dto.job.res;

import com.guardians.domain.job.entity.Job;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ResJobDto {
    private Long id;
    private String companyName;
    private String title;
    private String description;
    private String location;
    private String employmentType;
    private String careerLevel;
    private String salary;
    private LocalDate deadline;

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
}