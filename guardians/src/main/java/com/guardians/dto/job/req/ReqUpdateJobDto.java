package com.guardians.dto.job.req;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateJobDto {
    private String title;
    private String description;
    private String salary;
    private LocalDate deadline;
    private Boolean isActive;
}