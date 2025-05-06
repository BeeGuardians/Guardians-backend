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

    @Column(name = "icon_url")
    private String iconUrl;  // 뱃지 아이콘 이미지 S3 URL
}
