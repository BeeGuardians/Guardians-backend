package com.guardians.service.question;

import com.guardians.dto.question.req.ReqCreateQuestionDto;
import com.guardians.dto.question.req.ReqUpdateQuestionDto;
import com.guardians.dto.question.res.ResCreateQuestionDto;
import com.guardians.dto.question.res.ResUpdateQuestionDto;
import com.guardians.dto.question.res.ResQuestionDetailDto;
import com.guardians.dto.question.res.ResQuestionListDto;

import java.util.List;

public interface QuestionService {

    // 질문 등록
    ResCreateQuestionDto createQuestion(Long userId, ReqCreateQuestionDto dto);

    // 질문 전체 목록 조회
    List<ResQuestionListDto> getQuestionList();

    // 질문 단건 상세 조회
    ResQuestionDetailDto getQuestionDetail(Long questionId);

    // 질문 수정
    ResUpdateQuestionDto updateQuestion(Long userId, Long questionId, ReqUpdateQuestionDto dto);

    // 질문 삭제
    void deleteQuestion(Long userId, Long questionId);
}