package com.guardians.dto.wargame.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqSubmitFlagDto {
    @NotBlank(message = "플래그를 입력해주세요.")
    @Size(max = 500, message = "플래그는 500자 이하로 입력해주세요.")
    private String flag;
}
