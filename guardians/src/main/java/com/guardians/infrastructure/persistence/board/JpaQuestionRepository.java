package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface JpaQuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q JOIN FETCH q.user u JOIN FETCH q.wargame w WHERE q.id = :id")
    Optional<Question> findByIdWithUserAndWargame(@Param("id") Long id);

    @Query("SELECT q FROM Question q JOIN FETCH q.user u WHERE q.id = :id")
    Optional<Question> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT q FROM Question q JOIN FETCH q.user u JOIN FETCH q.wargame w ORDER BY q.createdAt DESC")
    List<Question> findAllWithUserAndWargame();

    @Query("SELECT q FROM Question q JOIN FETCH q.user u JOIN FETCH q.wargame w WHERE w.id = :wargameId")
    List<Question> findAllByWargameId(@Param("wargameId") Long wargameId);
}
