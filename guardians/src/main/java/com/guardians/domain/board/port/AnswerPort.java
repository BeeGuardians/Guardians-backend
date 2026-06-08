package com.guardians.domain.board.port;

import com.guardians.domain.board.entity.Answer;

import java.util.List;
import java.util.Optional;

public interface AnswerPort {
    Optional<Answer> findByIdWithUser(Long id);
    List<Answer> findAllWithUserByQuestionId(Long questionId);
    Answer save(Answer answer);
    void delete(Answer answer);
}
