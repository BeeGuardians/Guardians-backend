package com.guardians.dto.wargame.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResHotWargameDto {
    private Long wargameId;
    private String title;
    private Long solveCount;
}
