package com.guardians.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 유저 로그인 관련
    USER_NOT_FOUND(1001, "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(1002, "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 회원 가입 관련
    DUPLICATE_EMAIL(1101, "이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USERNAME(1102, "이미 사용 중인 아이디입니다.", HttpStatus.BAD_REQUEST),

    // 게시판 관련
    BOARD_NOT_FOUND(1201, "해당 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    UNAUTHORIZED(1301, "해당 작업을 수행할 권한이 없습니다.", HttpStatus.UNAUTHORIZED),


    // 기타
    NOT_VALID_ARGUMENT(9001, "입력 값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST)
    ;



    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

}
