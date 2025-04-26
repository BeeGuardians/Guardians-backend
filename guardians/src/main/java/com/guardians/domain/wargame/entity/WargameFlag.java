package com.guardians.domain.wargame.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wargame_flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WargameFlag {

    @Id
    @Column(name = "wargame_id")
    private Long wargameId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "wargame_id")
    private Wargame wargame;

    @Column(name = "flag", nullable = false)
    private String flag;
}
