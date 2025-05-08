package com.guardians.domain.board.repository;

import com.guardians.domain.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByUserIdAndBoardId(Long userId, Long boardId);
    void deleteByUserIdAndBoardId(Long userId, Long boardId);
}
