package com.guardians.infrastructure.persistence.board;

import com.guardians.domain.board.entity.Question;
import com.guardians.domain.board.port.QuestionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionAdapter implements QuestionPort {

    private final JpaQuestionRepository jpa;

    @Override
    public Optional<Question> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Question> findByIdWithUserAndWargame(Long id) {
        return jpa.findByIdWithUserAndWargame(id);
    }

    @Override
    public Optional<Question> findByIdWithUser(Long id) {
        return jpa.findByIdWithUser(id);
    }

    @Override
    public List<Question> findAllWithUserAndWargame() {
        return jpa.findAllWithUserAndWargame();
    }

    @Override
    public List<Question> findAllByWargameId(Long wargameId) {
        return jpa.findAllByWargameId(wargameId);
    }

    @Override
    public Question save(Question question) {
        return jpa.save(question);
    }

    @Override
    public void delete(Question question) {
        jpa.delete(question);
    }
}
