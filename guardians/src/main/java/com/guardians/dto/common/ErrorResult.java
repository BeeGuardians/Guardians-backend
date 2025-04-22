package com.guardians.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "에러 응답 데이터 구조")
public class ErrorResult {

    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;

    @Schema(description = "애플리케이션 에러 코드", example = "1001")
    private int errorCode;

    @Schema(description = "에러 메시지", example = "존재하지 않는 사용자입니다.")
    private String message;
}


