package com.guardians.domain.board.repository;

import com.guardians.domain.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //댓글 목록 조회용
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.board.id = :boardId")
    List<Comment> findByBoardIdWithUser(@Param("boardId") Long boardId);
    //특정댓글 수정, 삭제용
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.id = :commentId")
    Optional<Comment> findByIdWithUser(@Param("commentId") Long commentId);

    List<Comment> findByBoardIdOrderByCreatedAtAsc(Long boardId);

}
