package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaBoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByUserIdAndBoardId(Long userId, Long boardId);
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);
}
