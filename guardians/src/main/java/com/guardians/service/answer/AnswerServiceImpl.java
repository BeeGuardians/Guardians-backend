package com.guardians.service.answer;

import com.guardians.domain.board.entity.Answer;
import com.guardians.domain.board.entity.Question;
import com.guardians.domain.board.repository.AnswerRepository;
import com.guardians.domain.board.repository.QuestionRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.dto.answer.req.ReqCreateAnswerDto;
import com.guardians.dto.answer.req.ReqUpdateAnswerDto;
import com.guardians.dto.answer.res.ResAnswerDetailDto;
import com.guardians.dto.answer.res.ResAnswerListDto;
import com.guardians.dto.answer.res.ResCreateAnswerDto;
import com.guardians.dto.answer.res.ResUpdateAnswerDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Builder
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Override
    public ResCreateAnswerDto createAnswer(Long userId, ReqCreateAnswerDto dto) {
        // 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 질문 조회
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // 답변 생성
        Answer answer = Answer.builder()
                .content(dto.getContent())
                .user(user)
                .question(question)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 저장
        Answer saved = answerRepository.save(answer);

        // 결과 반환
        return ResCreateAnswerDto.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .build();
    }

    @Override
    public List<ResAnswerListDto> getAnswerListByQuestion(Long questionId) {
        // 특정 질문에 대한 답변 목록 조회
        List<Answer> answers = answerRepository.findAllByQuestionId(questionId);

        // DTO 변환
        return answers.stream()
                .map(a -> ResAnswerListDto.builder()
                        .id(a.getId())
                        .content(a.getContent())
                        .username(a.getUser().getUsername())
                        .createdAt(a.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ResUpdateAnswerDto updateAnswer(Long userId, Long answerId, ReqUpdateAnswerDto dto) {
        // 답변 조회
        Answer answer = answerRepository.findByIdWithUser(answerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        // 작성자 검증
        if (!answer.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 수정
        answer.setContent(dto.getContent());
        answer.setUpdatedAt(LocalDateTime.now());

        // 저장
        Answer updated = answerRepository.save(answer);

        // 결과 반환
        return ResUpdateAnswerDto.builder()
                .id(updated.getId())
                .content(updated.getContent())
                .build();
    }

    @Override
    public void deleteAnswer(Long userId, Long answerId) {
        // 답변 조회
        Answer answer = answerRepository.findByIdWithUser(answerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANSWER_NOT_FOUND));

        // 작성자 검증
        if (!answer.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 삭제
        answerRepository.delete(answer);
    }
}