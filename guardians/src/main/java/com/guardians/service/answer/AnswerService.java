package com.guardians.service.answer;

import com.guardians.dto.answer.req.ReqCreateAnswerDto;
import com.guardians.dto.answer.req.ReqUpdateAnswerDto;
import com.guardians.dto.answer.res.ResAnswerListDto;
import com.guardians.dto.answer.res.ResCreateAnswerDto;
import com.guardians.dto.answer.res.ResUpdateAnswerDto;

import java.util.List;

public interface AnswerService {

    // 답변 등록
    ResCreateAnswerDto createAnswer(Long userId, ReqCreateAnswerDto dto);

    // 특정 질문에 대한 답변 목록 조회
    List<ResAnswerListDto> getAnswerListByQuestion(Long questionId);

    // 답변 수정
    ResUpdateAnswerDto updateAnswer(Long userId, Long answerId, ReqUpdateAnswerDto dto);

    // 답변 삭제
    void deleteAnswer(Long userId, Long answerId);
}
