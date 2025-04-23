package com.guardians.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "성공 응답 데이터 구조")
public class SuccessResult {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;

    @Schema(description = "응답 데이터")
    private Object data;

    @Schema(description = "성공 메시지", example = "요청 성공")
    private String message;
}
