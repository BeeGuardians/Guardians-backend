package com.guardians.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "challenge_flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeFlag {

    @Id
    @Column(name = "challenge_id")
    private Long challengeId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Column(name = "flag", nullable = false)
    private String flag;
}
