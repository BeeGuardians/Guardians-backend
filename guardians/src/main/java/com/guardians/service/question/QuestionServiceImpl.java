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
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final WargameRepository wargameRepository;

    @Override
    public ResCreateQuestionDto createQuestion(Long userId, ReqCreateQuestionDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Wargame wargame = wargameRepository.findById(dto.getWargameId())
                .orElseThrow(() -> new CustomException(ErrorCode.WARGAME_NOT_FOUND));

        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .wargame(wargame)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .viewCount(0)
                .build();

        Question saved = questionRepository.save(question);

        return ResCreateQuestionDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .build();
    }

    @Override
    public List<ResQuestionListDto> getQuestionList() {
        List<Question> questions = questionRepository.findAllWithUserAndWargame();

        return questions.stream()
                .map(q -> ResQuestionListDto.builder()
                        .id(q.getId())
                        .title(q.getTitle())
                        .content(q.getContent())
                        .username(q.getUser().getUsername())
                        .wargameTitle(q.getWargame().getTitle())
                        .wargameId(q.getWargame().getId())
                        .createdAt(q.getCreatedAt())
                        .viewCount(q.getViewCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ResQuestionListDto> getQuestionsByWargame(Long wargameId) {
        List<Question> questions = questionRepository.findAllByWargameId(wargameId);

        return questions.stream()
                .map(q -> ResQuestionListDto.builder()
                        .id(q.getId())
                        .title(q.getTitle())
                        .content(q.getContent())
                        .username(q.getUser().getUsername())
                        .profileImageUrl(q.getUser().getProfileImageUrl())
                        .createdAt(q.getCreatedAt())
                        .viewCount(q.getViewCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ResQuestionDetailDto getQuestionDetail(Long questionId) {
        Question question = questionRepository.findByIdWithUserAndWargame(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        question.increaseViewCount();

        return ResQuestionDetailDto.builder()
                .id(question.getId())
                .userId(String.valueOf(question.getUser().getId()))
                .title(question.getTitle())
                .content(question.getContent())
                .username(question.getUser().getUsername())
                .wargameId(question.getWargame().getId())
                .wargameTitle(question.getWargame().getTitle())
                .profileImageUrl(question.getUser().getProfileImageUrl())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .viewCount(question.getViewCount())
                .build();
    }

    @Override
    public ResUpdateQuestionDto updateQuestion(Long userId, Long questionId, ReqUpdateQuestionDto dto) {
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        if (!question.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setUpdatedAt(LocalDateTime.now());

        Question updated = questionRepository.save(question);

        return ResUpdateQuestionDto.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .build();
    }

    @Override
    public void deleteQuestion(Long userId, Long questionId) {
        Question question = questionRepository.findByIdWithUser(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        if (!question.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        questionRepository.delete(question);
    }

    @Override
    public void increaseViewCount(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
        question.increaseViewCount();  // 🔥 엔티티 메서드 호출
    }


}
