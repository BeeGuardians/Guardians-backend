package com.guardians.domain.board.port;

import com.guardians.domain.board.entity.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentPort {
    List<Comment> findByBoardIdWithUser(Long boardId);
    Optional<Comment> findByIdWithUser(Long commentId);
    List<CommentCountProjection> countCommentsByBoard();
    Comment save(Comment comment);
    void delete(Comment comment);
}
