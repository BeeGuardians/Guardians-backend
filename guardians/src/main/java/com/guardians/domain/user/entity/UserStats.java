package com.guardians.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_stats_user"))
    private User user;

    private int score;

    @Column(name = "total_solved")
    private int totalSolved;

    @Column(name = "last_solved_at")
    private LocalDateTime lastSolvedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier;

    public void addScore(int scoreToAdd) {
        this.score += scoreToAdd;
        this.updatedAt = LocalDateTime.now();
        this.tier = Tier.fromScore(this.score);
    }
}
