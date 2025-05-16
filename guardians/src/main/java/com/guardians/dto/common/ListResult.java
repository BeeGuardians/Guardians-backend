package com.guardians.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리스트 응답 데이터 구조")
public class ListResult {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;

    @Schema(description = "리스트 데이터")
    private Object data;

    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "총 데이터 개수")
    private int count;
}
