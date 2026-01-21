package com.guardians.domain.wargame.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
    WEB("Web"),
    FORENSIC("Forensic"),
    CRYPTO("Crypto"),
    BRUTE_FORCE("BruteForce"),
    SOURCE_LEAK("SourceLeak");

    private final String displayName;

    public static CategoryType fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(type -> type.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown category name: " + displayName));
    }
}
