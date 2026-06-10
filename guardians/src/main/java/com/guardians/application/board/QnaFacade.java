package com.guardians.application.board;

import com.guardians.domain.board.entity.Answer;
import com.guardians.domain.board.entity.Question;
import com.guardians.domain.board.port.AnswerPort;
import com.guardians.domain.board.port.QuestionPort;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.port.UserPort;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.port.WargamePort;
import com.guardians.dto.answer.res.ResAnswerListDto;
import com.guardians.dto.answer.res.ResCreateAnswerDto;
import com.guardians.dto.answer.res.ResUpdateAnswerDto;
import com.guardians.dto.question.res.ResCreateQuestionDto;
import com.guardians.dto.question.res.ResQuestionDetailDto;
import com.guardians.dto.question.res.ResQuestionListDto;
import com.guardians.dto.question.res.ResUpdateQuestionDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaFacade {

    private final QuestionPort questionPort;
    private final AnswerPort answerPort;
    private final UserPort userPort;
    private final WargamePort wargamePort;

    @Transactional
    public ResCreateQuestionDto createQuestion(Long userId, String title, String content, Long wargameId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Wargame wargame = wargamePort.findById(wargameId)
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        Question question = Question.builder()
                .title(title)
                .content(content)
                .user(user)
                .wargame(wargame)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .viewCount(0)
                .build();

        return ResCreateQuestionDto.fromEntity(questionPort.save(question));
    }

    public List<ResQuestionListDto> getQuestionList() {
        return questionPort.findAllWithUserAndWargame().stream()
                .map(ResQuestionListDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ResQuestionListDto> getQuestionsByWargame(Long wargameId) {
        return questionPort.findAllByWargameId(wargameId).stream()
                .map(ResQuestionListDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ResQuestionDetailDto getQuestionDetail(Long questionId) {
        Question question = questionPort.findByIdWithUserAndWargame(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
        question.increaseViewCount();
        return ResQuestionDetailDto.fromEntity(question);
    }

    @Transactional
    public ResUpdateQuestionDto updateQuestion(Long userId, Long questionId, String title, String content) {
        Question question = questionPort.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        if (!question.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        question.update(title, content);
        return ResUpdateQuestionDto.fromEntity(question);
    }

    @Transactional
    public void deleteQuestion(Long userId, Long questionId) {
        Question question = questionPort.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        if (!question.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        questionPort.delete(question);
    }

    @Transactional
    public void increaseViewCount(Long questionId) {
        Question question = questionPort.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
        question.increaseViewCount();
    }

    @Transactional
    public ResCreateAnswerDto createAnswer(Long userId, Long questionId, String content) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Question question = questionPort.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        Answer answer = Answer.builder()
                .content(content)
                .user(user)
                .question(question)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ResCreateAnswerDto.fromEntity(answerPort.save(answer));
    }

    public List<ResAnswerListDto> getAnswerListByQuestion(Long questionId) {
        return answerPort.findAllWithUserByQuestionId(questionId).stream()
                .map(ResAnswerListDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResUpdateAnswerDto updateAnswer(Long userId, Long answerId, String content) {
        Answer answer = answerPort.findByIdWithUser(answerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        if (!answer.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        answer.updateContent(content);
        return ResUpdateAnswerDto.fromEntity(answer);
    }

    @Transactional
    public void deleteAnswer(Long userId, Long answerId) {
        Answer answer = answerPort.findByIdWithUser(answerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        if (!answer.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        answerPort.delete(answer);
    }

    public List<String> getWargameTitles() {
        return wargamePort.findAllWithCategory().stream()
                .map(Wargame::getTitle)
                .toList();
    }
}
