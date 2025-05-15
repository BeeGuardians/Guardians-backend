package com.guardians.dto.job.req;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReqUpdateJobDto {
    private String title;
    private String description;
    private String salary;
    private LocalDate deadline;
    private Boolean isActive;
}