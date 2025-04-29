package com.guardians.domain.board.repository;

import com.guardians.domain.board.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a JOIN FETCH a.user WHERE a.id = :id")
    Optional<Answer> findByIdWithUser(@Param("id") Long id);

    List<Answer> findAllByQuestionId(Long questionId);
}
