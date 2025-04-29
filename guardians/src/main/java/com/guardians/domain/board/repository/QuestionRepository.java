package com.guardians.domain.board.repository;

import com.guardians.domain.board.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 작성자(User) + 워게임(Wargame) 같이 조회 (단건)
    @Query("SELECT q FROM Question q JOIN FETCH q.user u JOIN FETCH q.wargame w WHERE q.id = :id")
    Optional<Question> findByIdWithUserAndWargame(@Param("id") Long id);

    // 작성자(User) 같이 조회 (단건) - 수정/삭제용
    @Query("SELECT q FROM Question q JOIN FETCH q.user u WHERE q.id = :id")
    Optional<Question> findByIdWithUser(@Param("id") Long id);

    // 전체 질문 목록 조회 (작성자, 워게임 함께)
    @Query("SELECT q FROM Question q JOIN FETCH q.user u JOIN FETCH q.wargame w")
    List<Question> findAllWithUserAndWargame();
}