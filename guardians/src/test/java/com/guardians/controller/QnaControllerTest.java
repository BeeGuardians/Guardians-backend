package com.guardians.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.domain.wargame.repository.WargameRepository;
import com.guardians.dto.answer.req.ReqCreateAnswerDto;
import com.guardians.dto.answer.req.ReqUpdateAnswerDto;
import com.guardians.dto.question.req.ReqCreateQuestionDto;
import com.guardians.dto.question.req.ReqUpdateQuestionDto;
import com.guardians.service.answer.AnswerService;
import com.guardians.service.question.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QnaController.class)
@TestPropertySource(properties = {
        "spring.session.store-type=none",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
})
class QnaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private WargameRepository wargameRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/qna/questions - 모든 질문 목록 조회")
    void getAllQuestions() throws Exception {
        mockMvc.perform(get("/api/qna/questions"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/qna/questions - 워게임 ID 포함 질문 작성")
    void createQuestion() throws Exception {
        long wargameId = 1L;

        ReqCreateQuestionDto request = new ReqCreateQuestionDto();
        request.setTitle("Test Question");
        request.setContent("This is a test question");
        request.setWargameId(wargameId);

        mockMvc.perform(post("/api/qna/questions")
                        .sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/qna/answers - 답변 작성")
    void createAnswer() throws Exception {
        ReqCreateAnswerDto request = new ReqCreateAnswerDto();
        request.setQuestionId(1L);
        request.setContent("This is an answer.");

        mockMvc.perform(post("/api/qna/answers")
                        .sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/qna/questions/{id} - 질문 수정")
    void updateQuestion() throws Exception {
        ReqUpdateQuestionDto dto = new ReqUpdateQuestionDto();
        dto.setTitle("Updated Title");
        dto.setContent("Updated content");

        mockMvc.perform(patch("/api/qna/questions/1")
                        .sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/qna/answers/{id} - 답변 수정")
    void updateAnswer() throws Exception {
        ReqUpdateAnswerDto dto = new ReqUpdateAnswerDto();
        dto.setContent("Updated answer");

        mockMvc.perform(patch("/api/qna/answers/1")
                        .sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/qna/questions/{id} - 질문 삭제")
    void deleteQuestion() throws Exception {
        mockMvc.perform(delete("/api/qna/questions/1")
                        .sessionAttr("userId", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/qna/answers/{id} - 답변 삭제")
    void deleteAnswer() throws Exception {
        mockMvc.perform(delete("/api/qna/answers/1")
                        .sessionAttr("userId", 1L))
                .andExpect(status().isOk());
    }
}
