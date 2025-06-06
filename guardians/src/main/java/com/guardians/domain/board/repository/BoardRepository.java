package com.guardians.domain.board.repository;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.BoardType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.boardType = :boardType")
    List<Board> findByBoardType(@Param("boardType") BoardType boardType);

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.id = :id")
    Optional<Board> findByIdWithUser(@Param("id") Long id);

    List<Board> findAllByUserId(Long userId); // 추가

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Board> findByBoardTypeAndKeyword(@Param("boardType") BoardType boardType,
                                          @Param("keyword") String keyword);


}
