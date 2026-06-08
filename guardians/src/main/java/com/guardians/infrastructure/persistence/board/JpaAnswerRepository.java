package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface JpaAnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a JOIN FETCH a.user WHERE a.id = :id")
    Optional<Answer> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT a FROM Answer a JOIN FETCH a.user u JOIN FETCH u.userStats WHERE a.question.id = :questionId ORDER BY a.createdAt ASC")
    List<Answer> findAllWithUserByQuestionId(@Param("questionId") Long questionId);
}
