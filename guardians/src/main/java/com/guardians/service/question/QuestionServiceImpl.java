package com.guardians.service.question;

import com.guardians.domain.board.entity.Question;
import com.guardians.domain.board.repository.QuestionRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.repository.WargameRepository;

import com.guardians.dto.question.req.ReqCreateQuestionDto;
import com.guardians.dto.question.req.ReqUpdateQuestionDto;

import com.guardians.dto.question.res.ResCreateQuestionDto;
import com.guardians.dto.question.res.ResUpdateQuestionDto;
import com.guardians.dto.question.res.ResQuestionDetailDto;
import com.guardians.dto.question.res.ResQuestionListDto;

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
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final WargameRepository wargameRepository;

    @Override
    public ResCreateQuestionDto createQuestion(Long userId, ReqCreateQuestionDto dto) {
        // 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 워게임 조회
        Wargame wargame = wargameRepository.findById(dto.getWargameId())
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        // 질문 생성
        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .wargame(wargame)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 저장
        Question saved = questionRepository.save(question);

        // 결과 반환
        return ResCreateQuestionDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .build();
    }

    @Override
    public List<ResQuestionListDto> getQuestionList() {
        // 전체 질문 목록 조회
        List<Question> questions = questionRepository.findAllWithUserAndWargame();

        // DTO 변환
        return questions.stream()
                .map(q -> ResQuestionListDto.builder()
                        .id(q.getId())
                        .title(q.getTitle())
                        .username(q.getUser().getUsername())
                        .createdAt(q.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ResQuestionDetailDto getQuestionDetail(Long questionId) {
        // 단건 질문 조회
        Question question = questionRepository.findByIdWithUserAndWargame(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // 결과 반환
        return ResQuestionDetailDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .username(question.getUser().getUsername())
                .wargameTitle(question.getWargame().getTitle())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    @Override
    public ResUpdateQuestionDto updateQuestion(Long userId, Long questionId, ReqUpdateQuestionDto dto) {
        // 질문 조회
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // 작성자 검증
        if (!question.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 수정
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setUpdatedAt(LocalDateTime.now());

        // 저장
        Question updated = questionRepository.save(question);

        // 결과 반환
        return ResUpdateQuestionDto.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .build();
    }

    @Override
    public void deleteQuestion(Long userId, Long questionId) {
        // 질문 조회
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        // 작성자 검증
        if (!question.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 삭제
        questionRepository.delete(question);
    }
}