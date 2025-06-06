package com.guardians.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.config.SecurityConfig;
import com.guardians.domain.wargame.repository.WargameRepository;
import com.guardians.dto.question.req.ReqCreateQuestionDto;
import com.guardians.dto.question.req.ReqUpdateQuestionDto;
import com.guardians.service.answer.AnswerService;
import com.guardians.service.question.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = QnaController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        excludeAutoConfiguration = RedisHttpSessionConfiguration.class
)
class QnaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private AnswerService answerService;

    @MockBean
    private WargameRepository wargameRepository;

    @Test
    @DisplayName("질문 작성 성공")
    @WithMockUser
    void createQuestion_success() throws Exception {
        ReqCreateQuestionDto request = new ReqCreateQuestionDto();
        request.setTitle("테스트 질문 제목");
        request.setContent("테스트 질문 내용입니다.");
        request.setWargameId(1L);

        mockMvc.perform(post("/api/qna/questions")
                        .sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<ReqCreateQuestionDto> requestDtoCaptor = ArgumentCaptor.forClass(ReqCreateQuestionDto.class);

        verify(questionService).createQuestion(userIdCaptor.capture(), requestDtoCaptor.capture());

        assertEquals(1L, userIdCaptor.getValue());
        assertEquals("테스트 질문 제목", requestDtoCaptor.getValue().getTitle());
    }

    @Test
    @DisplayName("질문 수정 성공")
    @WithMockUser
    void updateQuestion_success() throws Exception {
        long questionId = 1L;
        ReqUpdateQuestionDto request = new ReqUpdateQuestionDto();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        mockMvc.perform(patch("/api/qna/questions/" + questionId)
                        .sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> questionIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<ReqUpdateQuestionDto> requestDtoCaptor = ArgumentCaptor.forClass(ReqUpdateQuestionDto.class);

        verify(questionService).updateQuestion(userIdCaptor.capture(), questionIdCaptor.capture(), requestDtoCaptor.capture());

        assertEquals(1L, userIdCaptor.getValue());
        assertEquals(questionId, questionIdCaptor.getValue());
        assertEquals("수정된 제목", requestDtoCaptor.getValue().getTitle());
    }

    @Test
    @DisplayName("질문 삭제 성공")
    @WithMockUser
    void deleteQuestion_success() throws Exception {
        long questionId = 1L;

        mockMvc.perform(delete("/api/qna/questions/" + questionId)
                        .sessionAttr("userId", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(questionService).deleteQuestion(1L, questionId);
    }
}