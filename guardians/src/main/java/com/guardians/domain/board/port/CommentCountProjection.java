package com.guardians.domain.board.port;

public interface CommentCountProjection {
    Long getBoardId();
    Long getCommentCount();
}
