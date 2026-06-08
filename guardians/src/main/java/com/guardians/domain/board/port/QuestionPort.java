package com.guardians.domain.board.port;

import com.guardians.domain.board.entity.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionPort {
    Optional<Question> findById(Long id);
    Optional<Question> findByIdWithUserAndWargame(Long id);
    Optional<Question> findByIdWithUser(Long id);
    List<Question> findAllWithUserAndWargame();
    List<Question> findAllByWargameId(Long wargameId);
    Question save(Question question);
    void delete(Question question);
}
