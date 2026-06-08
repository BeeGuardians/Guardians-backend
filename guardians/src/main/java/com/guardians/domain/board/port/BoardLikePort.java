package com.guardians.domain.board.port;

import com.guardians.domain.board.entity.BoardLike;

import java.util.Optional;

public interface BoardLikePort {
    Optional<BoardLike> findByUserIdAndBoardId(Long userId, Long boardId);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
    BoardLike save(BoardLike boardLike);
    void delete(BoardLike boardLike);
}
