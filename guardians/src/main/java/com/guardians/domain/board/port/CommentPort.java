package com.guardians.domain.board.port;

import com.guardians.domain.board.entity.Comment;
import com.guardians.domain.board.port.CommentCountRepository;

import java.util.List;
import java.util.Optional;

public interface CommentPort {
    List<Comment> findByBoardIdWithUser(Long boardId);
    Optional<Comment> findByIdWithUser(Long commentId);
    List<CommentCountRepository> countCommentsByBoard();
    Comment save(Comment comment);
    void delete(Comment comment);
}
