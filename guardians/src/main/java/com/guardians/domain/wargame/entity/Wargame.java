package com.guardians.domain.wargame.entity;

import com.guardians.domain.board.entity.Question;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wargames")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wargame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String dockerImageUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private int score;

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Column(name = "file_url")
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "wargame", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private WargameFlag wargameFlag;

    @Builder.Default
    @OneToMany(mappedBy = "wargame", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WargameLike> wargameLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "wargame", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SolvedWargame> solvedWargames = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "wargame", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "wargame", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

}
