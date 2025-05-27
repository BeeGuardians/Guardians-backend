package com.guardians.controller;

import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.repository.WargameRepository;
import com.guardians.dto.answer.req.ReqCreateAnswerDto;
import com.guardians.dto.answer.req.ReqUpdateAnswerDto;
import com.guardians.dto.answer.res.ResAnswerListDto;
import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.question.req.ReqCreateQuestionDto;
import com.guardians.dto.question.req.ReqUpdateQuestionDto;
import com.guardians.dto.question.res.ResQuestionDetailDto;
import com.guardians.dto.question.res.ResQuestionListDto;
import com.guardians.service.answer.AnswerService;
import com.guardians.service.question.QuestionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final WargameRepository wargameRepository;

    // 질문 작성
    @PostMapping("/questions")
    public ResponseEntity<ResWrapper<?>> createQuestion(
            HttpSession session,
            @RequestBody @Valid ReqCreateQuestionDto dto) {
        Long userId = (Long) session.getAttribute("userId");
        questionService.createQuestion(userId, dto);
        return ResponseEntity.ok(ResWrapper.resSuccess("질문 등록 완료", null));
    }

    // 모든 질문 목록 조회
    @GetMapping("/questions")
    public ResponseEntity<ResWrapper<?>> getAllQuestions() {
        List<ResQuestionListDto> response = questionService.getQuestionList();
        return ResponseEntity.ok(ResWrapper.resSuccess("전체 질문 목록 조회 성공", response));
    }

    // 특정 워게임 질문 목록 조회
    @GetMapping("/wargames/{wargameId}/questions")
    public ResponseEntity<ResWrapper<?>> getQuestionsByWargame(@PathVariable Long wargameId) {
        List<ResQuestionListDto> response = questionService.getQuestionsByWargame(wargameId);
        return ResponseEntity.ok(ResWrapper.resSuccess("워게임 질문 목록 조회 성공", response));
    }


    // 질문 단건 상세 조회
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<ResWrapper<?>> getQuestionDetail(@PathVariable Long questionId) {
        ResQuestionDetailDto response = questionService.getQuestionDetail(questionId);
        return ResponseEntity.ok(ResWrapper.resSuccess("질문 상세 조회 성공", response));
    }

    // 질문 수정
    @PatchMapping("/questions/{questionId}")
    public ResponseEntity<ResWrapper<?>> updateQuestion(
            HttpSession session,
            @PathVariable Long questionId,
            @RequestBody ReqUpdateQuestionDto dto) {
        Long userId = (Long) session.getAttribute("userId");
        questionService.updateQuestion(userId, questionId, dto);
        return ResponseEntity.ok(ResWrapper.resSuccess("질문 수정 완료", null));
    }

    // 질문 삭제
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ResWrapper<?>> deleteQuestion(
            HttpSession session,
            @PathVariable Long questionId) {
        Long userId = (Long) session.getAttribute("userId");
        questionService.deleteQuestion(userId, questionId);
        return ResponseEntity.ok(ResWrapper.resSuccess("질문 삭제 완료", null));
    }

    // 답변 작성
    @PostMapping("/answers")
    public ResponseEntity<ResWrapper<?>> createAnswer(
            HttpSession session,
            @RequestBody ReqCreateAnswerDto dto) {
        Long userId = (Long) session.getAttribute("userId");
        answerService.createAnswer(userId, dto);
        return ResponseEntity.ok(ResWrapper.resSuccess("답변 등록 완료", null));
    }

    // 답변 목록 조회 (특정 질문 기준)
    @GetMapping("/answers/{questionId}")
    public ResponseEntity<ResWrapper<?>> getAnswerListByQuestion(@PathVariable Long questionId) {
        List<ResAnswerListDto> response = answerService.getAnswerListByQuestion(questionId);
        return ResponseEntity.ok(ResWrapper.resList("답변 목록 조회 성공", response, response.size()));
    }

    // 답변 수정
    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<ResWrapper<?>> updateAnswer(
            HttpSession session,
            @PathVariable Long answerId,
            @RequestBody ReqUpdateAnswerDto dto) {
        Long userId = (Long) session.getAttribute("userId");
        answerService.updateAnswer(userId, answerId, dto);

        return ResponseEntity.ok(ResWrapper.resSuccess("답변 수정 완료", null));
    }

    // 답변 삭제
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<ResWrapper<?>> deleteAnswer(
            HttpSession session,
            @PathVariable Long answerId) {
        Long userId = (Long) session.getAttribute("userId");
        answerService.deleteAnswer(userId, answerId);
        return ResponseEntity.ok(ResWrapper.resSuccess("답변 삭제 완료", null));
    }

    // 워게임 목록 조회
    @GetMapping("/wargames")
    public ResponseEntity<ResWrapper<?>> getWargameTitles() {
        List<String> titles = wargameRepository.findAll().stream()
                .map(Wargame::getTitle)
                .toList();
        return ResponseEntity.ok(ResWrapper.resSuccess("워게임 목록 조회 성공", titles));
    }
}

