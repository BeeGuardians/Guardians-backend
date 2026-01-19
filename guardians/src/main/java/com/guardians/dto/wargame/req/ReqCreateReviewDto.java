package com.guardians.dto.wargame.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateReviewDto {
    @NotBlank(message = "리뷰 내용을 입력해주세요.")
    @Size(min = 5, max = 2000, message = "리뷰는 5자 이상 2000자 이하로 입력해주세요.")
    private String content;
}
