package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.BoardType;
import com.guardians.domain.board.port.BoardPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardAdapter implements BoardPort {

    private final JpaBoardRepository jpa;

    @Override
    public Optional<Board> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Board> findByIdWithUser(Long id) {
        return jpa.findByIdWithUser(id);
    }

    @Override
    public List<Board> findByBoardType(BoardType boardType) {
        return jpa.findByBoardType(boardType);
    }

    @Override
    public List<Board> findByBoardTypeAndKeyword(BoardType boardType, String keyword) {
        return jpa.findByBoardTypeAndKeyword(boardType, keyword);
    }

    @Override
    public List<Board> findAllByUserId(Long userId) {
        return jpa.findAllByUserId(userId);
    }

    @Override
    public List<Board> findTop10ByHotScore() {
        return jpa.findTop10ByHotScore();
    }

    @Override
    public Board save(Board board) {
        return jpa.save(board);
    }

    @Override
    public void delete(Board board) {
        jpa.delete(board);
    }
}
