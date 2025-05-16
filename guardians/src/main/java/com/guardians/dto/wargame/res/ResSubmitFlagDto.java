package com.guardians.dto.wargame.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResSubmitFlagDto {
    private boolean correct;
    private String message;
}
