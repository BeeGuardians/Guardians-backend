package com.guardians.service.question;

import com.guardians.dto.question.res.ResCreateQuestionDto;
import com.guardians.dto.question.res.ResQuestionDetailDto;
import com.guardians.dto.question.res.ResQuestionListDto;
import com.guardians.dto.question.res.ResUpdateQuestionDto;

import java.util.List;

public interface QuestionService {

    ResCreateQuestionDto createQuestion(Long userId, String title, String content, Long wargameId);

    List<ResQuestionListDto> getQuestionList();

    List<ResQuestionListDto> getQuestionsByWargame(Long wargameId);

    ResQuestionDetailDto getQuestionDetail(Long questionId);

    ResUpdateQuestionDto updateQuestion(Long userId, Long questionId, String title, String content);

    void deleteQuestion(Long userId, Long questionId);

    void increaseViewCount(Long questionId);

}
