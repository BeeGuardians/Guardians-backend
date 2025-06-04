package com.guardians.service.question;

import com.guardians.dto.question.req.ReqCreateQuestionDto;
import com.guardians.dto.question.req.ReqUpdateQuestionDto;
import com.guardians.dto.question.res.ResCreateQuestionDto;
import com.guardians.dto.question.res.ResQuestionDetailDto;
import com.guardians.dto.question.res.ResQuestionListDto;
import com.guardians.dto.question.res.ResUpdateQuestionDto;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.Serializable;
import java.util.List;

public interface QuestionService {

    ResCreateQuestionDto createQuestion(Long userId, ReqCreateQuestionDto dto);

    List<ResQuestionListDto> getQuestionList();

    List<ResQuestionListDto> getQuestionsByWargame(Long wargameId); // 추가

    ResQuestionDetailDto getQuestionDetail(Long questionId);

    ResUpdateQuestionDto updateQuestion(Long userId, Long questionId, ReqUpdateQuestionDto dto);

    void deleteQuestion(Long userId, Long questionId);

    void increaseViewCount(Long questionId);

}
