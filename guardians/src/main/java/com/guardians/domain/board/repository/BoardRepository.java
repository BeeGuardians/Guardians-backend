package com.guardians.domain.board.repository;

import com.guardians.domain.board.entitiy.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // List<Board> findByUserId(Long userId);
    // List<Board> findByTitleContaining(String keyword);
}
