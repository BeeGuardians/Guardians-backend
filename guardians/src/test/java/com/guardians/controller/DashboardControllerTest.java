package com.guardians.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.dto.dashboard.ResRadarChartDto;
import com.guardians.exception.GlobalExceptionHandler;
import com.guardians.service.dashboard.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("카테고리별 실력 점수 조회 - 성공")
    void getRadarChart_Success() throws Exception {
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        List<ResRadarChartDto.CategoryScore> scores = Collections.singletonList(
                new ResRadarChartDto.CategoryScore("Web", 80.0)
        );
        given(dashboardService.calculateRadarChart(userId)).willReturn(scores);

        mockMvc.perform(get("/api/users/{userId}/dashboard/radar", userId)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("카테고리별 실력 점수 조회 성공"))
                .andExpect(jsonPath("$.result.data[0].category").value("Web"))
                .andExpect(jsonPath("$.result.data[0].normalizedScore").value(80.0))
                .andExpect(jsonPath("$.result.count").value(1))
                .andDo(print());
    }
}
