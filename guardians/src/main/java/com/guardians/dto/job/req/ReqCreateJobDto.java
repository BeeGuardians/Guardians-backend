package com.guardians.dto.job.req;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreateJobDto {
    @NotBlank(message = "회사명을 입력해주세요.")
    @Size(max = 100, message = "회사명은 100자 이하로 입력해주세요.")
    private String companyName;

    @NotBlank(message = "채용 제목을 입력해주세요.")
    @Size(max = 200, message = "채용 제목은 200자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "채용 설명을 입력해주세요.")
    @Size(max = 10000, message = "채용 설명은 10000자 이하로 입력해주세요.")
    private String description;

    @Size(max = 100, message = "근무지는 100자 이하로 입력해주세요.")
    private String location;

    @Size(max = 50, message = "고용 형태는 50자 이하로 입력해주세요.")
    private String employmentType;

    @Size(max = 50, message = "경력 수준은 50자 이하로 입력해주세요.")
    private String careerLevel;

    @Size(max = 100, message = "급여 정보는 100자 이하로 입력해주세요.")
    private String salary;

    @Future(message = "마감일은 오늘 이후 날짜로 설정해주세요.")
    private LocalDate deadline;

    @URL(message = "유효한 URL 형식이 아닙니다.")
    @Size(max = 500, message = "URL은 500자 이하로 입력해주세요.")
    private String sourceUrl;
}
