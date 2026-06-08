package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.Answer;
import com.guardians.domain.board.port.AnswerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AnswerAdapter implements AnswerPort {

    private final JpaAnswerRepository jpa;

    @Override
    public Optional<Answer> findByIdWithUser(Long id) {
        return jpa.findByIdWithUser(id);
    }

    @Override
    public List<Answer> findAllWithUserByQuestionId(Long questionId) {
        return jpa.findAllWithUserByQuestionId(questionId);
    }

    @Override
    public Answer save(Answer answer) {
        return jpa.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        jpa.delete(answer);
    }
}
