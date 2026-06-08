package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.BoardLike;
import com.guardians.domain.board.port.BoardLikePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardLikeAdapter implements BoardLikePort {

    private final JpaBoardLikeRepository jpa;

    @Override
    public Optional<BoardLike> findByUserIdAndBoardId(Long userId, Long boardId) {
        return jpa.findByUserIdAndBoardId(userId, boardId);
    }

    @Override
    public boolean existsByBoardIdAndUserId(Long boardId, Long userId) {
        return jpa.existsByBoardIdAndUserId(boardId, userId);
    }

    @Override
    public BoardLike save(BoardLike boardLike) {
        return jpa.save(boardLike);
    }

    @Override
    public void delete(BoardLike boardLike) {
        jpa.delete(boardLike);
    }
}
