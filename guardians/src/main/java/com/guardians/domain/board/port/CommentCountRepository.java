package com.guardians.domain.board.port;

public interface CommentCountRepository {
    Long getBoardId();
    Long getCommentCount();
}
