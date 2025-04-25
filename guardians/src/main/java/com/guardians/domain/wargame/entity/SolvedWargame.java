package com.guardians.domain.wargame.entity;

import com.guardians.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solved_wargames")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(SolvedWargameId.class)
public class SolvedWargame {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wargame_id")
    private Wargame wargame;

    @Column(name = "solved_at")
    private LocalDateTime solvedAt;
}
