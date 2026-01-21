package com.guardians.domain.badge.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BadgeType {
    BEGINNER("입문자"),
    WARGAME_MASTER("워게임 마스터"),
    HELL_SURVIVOR("지옥을 맛본 자"),
    WEB_HACKER("웹 해커"),
    DIGITAL_TRACKER("디지털 추적자"),
    CRYPTO_BREAKER("암호 해독자"),
    BRUTE_FORCER("무차별 해커"),
    LEAK_INTRUDER("정보 침투자"),
    EXPLORER("탐험가"),
    DAILY_GRINDER("꾸준한 해커"),
    FIRST_BLOOD("퍼스트 블러드");

    private final String displayName;

    public static BadgeType fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(type -> type.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown badge name: " + displayName));
    }
}
