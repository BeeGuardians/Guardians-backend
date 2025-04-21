package com.guardians.exception;

import com.guardians.dto.common.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResult> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResult errorResult = ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.NOT_VALID_ARGUMENT.getCode())
                .message(String.join(", ", errors))
                .build();

        return ResponseEntity.status(ErrorCode.NOT_VALID_ARGUMENT.getHttpStatus()).body(errorResult);
    }

    // RequestParam - 필수 파라미터 누락 or 이름 틀림
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResult> handleMissingParam(MissingServletRequestParameterException e) {
        ErrorResult errorResult = ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.NOT_VALID_ARGUMENT.getCode())
                .message(String.join( ", ", ErrorCode.NOT_VALID_ARGUMENT.getMessage(), e.getMessage()))
                .build();

        return ResponseEntity.status(ErrorCode.NOT_VALID_ARGUMENT.getHttpStatus()).body(errorResult);
    }

    // RequestParam 파라미터 타입이 잘못된 경우 (예: id=abc, but id is Long)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResult> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        ErrorResult errorResult = ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.NOT_VALID_ARGUMENT.getCode())
                .message(String.join( ", ", ErrorCode.NOT_VALID_ARGUMENT.getMessage(), e.getMessage()))
                .build();

        return ResponseEntity.status(ErrorCode.NOT_VALID_ARGUMENT.getHttpStatus()).body(errorResult);
    }


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResult> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();

        ErrorResult error = new ErrorResult(
                code.getHttpStatus().value(),
                code.getCode(),
                code.getMessage()
        );

        return ResponseEntity.status(code.getHttpStatus()).body(error);
    }
}
