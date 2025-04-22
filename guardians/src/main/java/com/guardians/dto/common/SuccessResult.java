package com.guardians.dto.common;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResult {
    private int status;
    private Object data;
    private String message;
}
