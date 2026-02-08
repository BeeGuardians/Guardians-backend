package com.guardians.service.answer;

import com.guardians.dto.answer.res.ResAnswerListDto;
import com.guardians.dto.answer.res.ResCreateAnswerDto;
import com.guardians.dto.answer.res.ResUpdateAnswerDto;

import java.util.List;

public interface AnswerService {

    ResCreateAnswerDto createAnswer(Long userId, Long questionId, String content);

    List<ResAnswerListDto> getAnswerListByQuestion(Long questionId);

    ResUpdateAnswerDto updateAnswer(Long userId, Long answerId, String content);

    void deleteAnswer(Long userId, Long answerId);
}
