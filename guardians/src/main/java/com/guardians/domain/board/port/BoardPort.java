package com.guardians.domain.board.port;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.BoardType;

import java.util.List;
import java.util.Optional;

public interface BoardPort {
    Optional<Board> findById(Long id);
    Optional<Board> findByIdWithUser(Long id);
    List<Board> findByBoardType(BoardType boardType);
    List<Board> findByBoardTypeAndKeyword(BoardType boardType, String keyword);
    List<Board> findAllByUserId(Long userId);
    List<Board> findTop10ByHotScore();
    Board save(Board board);
    void delete(Board board);
}
