package com.guardians.domain.board.repository;

import com.guardians.domain.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 커스텀 쿼리 예시
    // List<Comment> findByPostId(Long postId);
}
