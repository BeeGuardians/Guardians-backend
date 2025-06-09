package com.guardians.dto.dashboard;

import java.time.LocalDate;

public record ResScoreTrendDto(
        LocalDate date,
        int earnedScore
) {}