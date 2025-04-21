package com.guardians.dto.common;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResult {
    private int status;
    private int errorCode;
    private String message;
}

