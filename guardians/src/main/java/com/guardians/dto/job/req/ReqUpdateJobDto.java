package com.guardians.dto.job.req;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateJobDto {
    @Size(max = 200, message = "채용 제목은 200자 이하로 입력해주세요.")
    private String title;

    @Size(max = 10000, message = "채용 설명은 10000자 이하로 입력해주세요.")
    private String description;

    @Size(max = 100, message = "급여 정보는 100자 이하로 입력해주세요.")
    private String salary;

    @Future(message = "마감일은 오늘 이후 날짜로 설정해주세요.")
    private LocalDate deadline;

    private Boolean isActive;
}
