package com.guardians.domain.wargame.entity;

import com.guardians.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "wargame_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "wargame_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WargameLike implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요 누른 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wargame_likes_user"))
    private User user;

    // 좋아요 대상 챌린지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wargame_id", nullable = false)
    private Wargame wargame;
}
