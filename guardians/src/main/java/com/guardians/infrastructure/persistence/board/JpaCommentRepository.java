package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.Comment;
import com.guardians.domain.board.port.CommentCountRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface JpaCommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.board.id = :boardId ORDER BY c.createdAt ASC")
    List<Comment> findByBoardIdWithUser(@Param("boardId") Long boardId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.id = :commentId")
    Optional<Comment> findByIdWithUser(@Param("commentId") Long commentId);

    @Query("SELECT c.board.id AS boardId, COUNT(c.id) AS commentCount FROM Comment c GROUP BY c.board.id")
    List<CommentCountRepository> countCommentsByBoard();
}
