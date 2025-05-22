package com.guardians.domain.badge.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // 뱃지 이름

    @Column(nullable = false)
    private String description;  // 뱃지 설명

    @Column(name = "true_icon_url")
    private String trueIconUrl;  // 획득했을 때 보여줄 이미지 URL

    @Column(name = "false_icon_url")
    private String falseIconUrl;  // 미획득일 때 보여줄 이미지 URL
}
