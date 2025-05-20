package com.guardians.dto.wargame.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResUserStatusDto {
    private String username;
    private String startedAt;
    private boolean isFirstSolver;
}
