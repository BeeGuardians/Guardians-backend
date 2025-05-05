package com.guardians.domain.board.entity;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Wargame;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_questions_user"))
    private User user; // 질문 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wargame_id", nullable = false)
    private Wargame wargame; // 질문 대상 워게임

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
