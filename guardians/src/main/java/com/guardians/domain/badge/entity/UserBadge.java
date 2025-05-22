package com.guardians.domain.badge.entity;

import com.guardians.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_badge", columnNames = {"user_id", "badge_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_badges_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_badges_badge"))
    private Badge badge;

    @Column(name = "awarded_at", nullable = false)
    private LocalDateTime awardedAt;

    @PrePersist
    public void onCreate() {
        this.awardedAt = LocalDateTime.now();
    }
}
