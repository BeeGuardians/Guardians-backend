package com.guardians.domain.board.entity;

import com.guardians.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "board_likes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_board_like_user_board", columnNames = {"user_id", "board_id"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_board_like_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, foreignKey = @ForeignKey(name = "fk_board_like_board"))
    private Board board;

    public static BoardLike of(User user, Board board) {
        return BoardLike.builder()
                .user(user)
                .board(board)
                .build();
    }
}
