package com.guardians.dto.common;

import com.guardians.exception.CustomException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API 응답 래퍼")
public class ResWrapper<T> {

    @Schema(description = "결과 데이터 (성공 또는 실패 응답)", implementation = SuccessResult.class)
    private T result;

    public static ResWrapper<SuccessResult> resSuccess(String message, Object o) {
        return ResWrapper.<SuccessResult>builder()
                .result(SuccessResult.builder()
                        .status(200)
                        .message(message)
                        .data(o)
                        .build())
                .build();
    }

    public static ResWrapper<ListResult> resList(String message, Object list, int count) {
        return ResWrapper.<ListResult>builder()
                .result(ListResult.builder()
                        .status(200)
                        .message(message)
                        .data(list)
                        .count(count)
                        .build())
                .build();
    }

    public static ResWrapper<ErrorResult> resException(Exception e) {
        return ResWrapper.<ErrorResult>builder()
                .result(ErrorResult.builder()
                        .status(499)
                        .errorCode(9999)
                        .message(e.getMessage())
                        .build())
                .build();
    }

    public static ResWrapper<ErrorResult> resCustomException(CustomException e) {
        return ResWrapper.<ErrorResult>builder()
                .result(ErrorResult.builder()
                        .status(e.getErrorCode().getHttpStatus().value())
                        .errorCode(e.getErrorCode().getCode())
                        .message(e.getErrorCode().getMessage())
                        .build())
                .build();
    }

    public static ResWrapper<ErrorResult> resError(String message) {
        return ResWrapper.<ErrorResult>builder()
                .result(ErrorResult.builder()
                        .status(400)
                        .errorCode(1001)
                        .message(message)
                        .build())
                .build();
    }


}
