package com.guardians.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.wargame.req.ReqCreateWargameDto;
import com.guardians.dto.wargame.req.ReqSubmitFlagDto;
import com.guardians.dto.wargame.res.*;
import com.guardians.service.wargame.KubernetesKaliPodServiceImpl;
import com.guardians.service.wargame.KubernetesPodService;
import com.guardians.service.wargame.WargameService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class WargameControllerTest {

    @Mock
    private WargameService wargameService;

    @Mock
    private KubernetesPodService kubernetesPodService;

    @Mock
    private KubernetesKaliPodServiceImpl kubernetesKaliPodService;

    @InjectMocks
    private WargameController wargameController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(wargameController).build();
    }

    @Test
    @DisplayName("워게임 목록 조회 - 성공")
    void getWargameList_Success() throws Exception {
        List<ResWargameListDto> mockList = List.of(
                ResWargameListDto.builder()
                        .id(1L)
                        .title("Test Wargame")
                        .score(100)
                        .solved(false)
                        .bookmarked(false)
                        .liked(false)
                        .build()
        );

        given(wargameService.getWargameList(null)).willReturn(mockList);

        mockMvc.perform(get("/api/wargames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("워게임 목록 조회 성공"))
                .andExpect(jsonPath("$.result.count").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("워게임 상세 조회 - 성공")
    void getWargameById_Success() throws Exception {
        Long userId = 1L;
        Long wargameId = 100L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        ResWargameListDto dto = ResWargameListDto.builder()
                .id(wargameId)
                .title("Wargame Detail")
                .build();

        given(wargameService.getWargameById(userId, wargameId)).willReturn(dto);

        mockMvc.perform(get("/api/wargames/{wargameId}", wargameId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("워게임 상세 조회 성공"))
                .andExpect(jsonPath("$.result.data.id").value(wargameId))
                .andDo(print());
    }

    @Test
    @DisplayName("워게임 채점 제출 - 성공")
    void submitFlag_Success() throws Exception {
        Long userId = 1L;
        Long wargameId = 10L;
        String flag = "correct_flag";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        ReqSubmitFlagDto req = new ReqSubmitFlagDto();
        req.setFlag(flag);

        ResSubmitFlagDto resultDto = ResSubmitFlagDto.builder()
                .correct(true)
                .message("정답입니다!")
                .build();

        given(wargameService.submitFlag(userId, wargameId, flag)).willReturn(resultDto);

        mockMvc.perform(post("/api/wargames/{id}/submit", wargameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("채점 완료"))
                .andExpect(jsonPath("$.result.data.correct").value(true))
                .andDo(print());
    }
}
