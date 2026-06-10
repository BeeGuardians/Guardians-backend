package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.Comment;
import com.guardians.domain.board.port.CommentPort;
import com.guardians.domain.board.port.CommentCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentAdapter implements CommentPort {

    private final JpaCommentRepository jpa;

    @Override
    public List<Comment> findByBoardIdWithUser(Long boardId) {
        return jpa.findByBoardIdWithUser(boardId);
    }

    @Override
    public Optional<Comment> findByIdWithUser(Long commentId) {
        return jpa.findByIdWithUser(commentId);
    }

    @Override
    public List<CommentCountProjection> countCommentsByBoard() {
        return jpa.countCommentsByBoard();
    }

    @Override
    public Comment save(Comment comment) {
        return jpa.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        jpa.delete(comment);
    }
}
