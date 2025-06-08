package com.guardians.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.config.SecurityConfig;
import com.guardians.dto.board.req.ReqCreateCommentDto;
import com.guardians.dto.board.req.ReqUpdateCommentDto;
import com.guardians.dto.board.res.ResCommentListDto;
import com.guardians.service.board.CommentService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = CommentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        excludeAutoConfiguration = RedisHttpSessionConfiguration.class
)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("댓글 작성 성공")
    @WithMockUser
    void createComment_success() throws Exception {
        Long boardId = 1L;
        Long userId = 1L;
        ReqCreateCommentDto request = new ReqCreateCommentDto();
        request.setContent("테스트 댓글 내용");

        mockMvc.perform(post("/api/boards/{boardId}/comments", boardId)
                        .sessionAttr("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        ArgumentCaptor<ReqCreateCommentDto> dtoCaptor = ArgumentCaptor.forClass(ReqCreateCommentDto.class);
        verify(commentService).createComment(eq(userId), eq(boardId), dtoCaptor.capture());
        assertEquals(request.getContent(), dtoCaptor.getValue().getContent());
    }

    @Test
    @DisplayName("댓글 목록 조회 성공")
    @WithMockUser
    void getCommentList_success() throws Exception {
        Long boardId = 1L;
        LocalDateTime now = LocalDateTime.now();

        ResCommentListDto commentDto = ResCommentListDto.builder()
                .commentId(1L)
                .content("테스트 댓글입니다.")
                .username("testuser")
                .profileImageUrl("http://example.com/profile.jpg")
                .createdAt(now)
                .updatedAt(now)
                .userId(2L)
                .build();

        List<ResCommentListDto> resultList = Collections.singletonList(commentDto);

        given(commentService.getCommentsByBoard(boardId)).willReturn(resultList);

        mockMvc.perform(get("/api/boards/{boardId}/comments", boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data").isArray())
                .andExpect(jsonPath("$.result.data[0].commentId").value(1L))
                .andExpect(jsonPath("$.result.data[0].content").value("테스트 댓글입니다."))
                .andExpect(jsonPath("$.result.data[0].username").value("testuser"))
                .andExpect(jsonPath("$.result.data[0].userId").value(2L))
                .andDo(print());

        verify(commentService).getCommentsByBoard(boardId);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void updateComment_success() throws Exception {
        Long boardId = 1L;
        Long commentId = 1L;
        Long userId = 1L;
        ReqUpdateCommentDto request = new ReqUpdateCommentDto();
        request.setContent("수정된 댓글 내용");

        mockMvc.perform(patch("/api/boards/{boardId}/comments/{commentId}", boardId, commentId)
                        .sessionAttr("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        ArgumentCaptor<ReqUpdateCommentDto> dtoCaptor = ArgumentCaptor.forClass(ReqUpdateCommentDto.class);
        verify(commentService).updateComment(eq(userId), eq(commentId), dtoCaptor.capture());
        assertEquals(request.getContent(), dtoCaptor.getValue().getContent());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    @WithMockUser
    void deleteComment_success() throws Exception {
        Long boardId = 1L;
        Long commentId = 1L;
        Long userId = 1L;

        mockMvc.perform(delete("/api/boards/{boardId}/comments/{commentId}", boardId, commentId)
                        .sessionAttr("userId", userId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(commentService).deleteComment(userId, commentId);
    }
}