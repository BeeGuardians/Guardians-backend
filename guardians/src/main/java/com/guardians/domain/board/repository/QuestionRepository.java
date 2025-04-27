package com.guardians.domain.board.repository;

import com.guardians.domain.board.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
