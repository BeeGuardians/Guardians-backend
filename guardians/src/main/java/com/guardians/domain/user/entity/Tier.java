package com.guardians.domain.user.entity;

public enum Tier {
    BRONZE, SILVER, GOLD, PLATINUM;

    public static Tier fromScore(int score) {
        if (score >= 5000) return PLATINUM;
        if (score >= 3000) return GOLD;
        if (score >= 2000) return SILVER;
        return BRONZE;
    }
}
