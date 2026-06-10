package com.guardians.domain.wargame.port;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HotWargameResult {
    private final Long wargameId;
    private final String title;
    private final Long solveCount;
}
