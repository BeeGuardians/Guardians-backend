package com.guardians.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardians.domain.job.entity.Job;
import com.guardians.domain.user.entity.User;
import com.guardians.dto.job.req.ReqCreateJobDto;
import com.guardians.dto.job.req.ReqUpdateJobDto;
import com.guardians.dto.job.res.ResJobDto;
import com.guardians.dto.job.res.ResJobListDto;
import com.guardians.service.job.JobService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(value = JobController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @MockBean
    private com.guardians.domain.user.repository.UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private void mockAdminUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(
                User.builder()
                        .id(1L)
                        .username("admin")
                        .email("admin@example.com")
                        .role("ADMIN")
                        .profileImageUrl("http://example.com/profile.jpg")
                        .build()
        ));
    }

    @Test
    @DisplayName("채용공고 등록 성공")
    void createJob_success() throws Exception {
        ReqCreateJobDto dto = ReqCreateJobDto.builder()
                .companyName("카카오")
                .title("백엔드 개발자")
                .description("Spring Boot")
                .location("판교")
                .employmentType("정규직")
                .careerLevel("신입")
                .salary("4000만원")
                .deadline(LocalDate.now().plusDays(7))
                .sourceUrl("https://kakao.jobs")
                .build();

        mockAdminUser();

        mockMvc.perform(post("/api/jobs").sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("[관리자] 채용공고 등록 완료"));

        verify(jobService).createJob(any());
    }

    @Test
    @DisplayName("채용공고 수정 성공")
    void updateJob_success() throws Exception {
        ReqUpdateJobDto dto = ReqUpdateJobDto.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .salary("6000만원")
                .deadline(LocalDate.now().plusDays(5))
                .isActive(true)
                .build();

        mockAdminUser();

        mockMvc.perform(patch("/api/jobs/1").sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("[관리자] 채용공고 수정 완료"));

        verify(jobService).updateJob(eq(1L), any());
    }

    @Test
    @DisplayName("채용공고 삭제 성공")
    void deleteJob_success() throws Exception {
        mockAdminUser();

        mockMvc.perform(delete("/api/jobs/1").sessionAttr("userId", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("[관리자] 채용공고 삭제 완료"));

        verify(jobService).deleteJob(1L);
    }

    @Test
    @DisplayName("채용공고 목록 조회 성공")
    void getJobList_success() throws Exception {
        ResJobListDto job = ResJobListDto.builder()
                .jobId(1L)
                .title("백엔드 개발자")
                .companyName("카카오")
                .location("판교")
                .employmentType("정규직")
                .careerLevel("경력")
                .deadline(LocalDate.now().plusDays(7))
                .sourceUrl("http://kakao.com/job")
                .build();

        when(jobService.getJobList()).thenReturn(List.of(job));

        mockMvc.perform(get("/api/jobs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("채용공고 목록 조회 성공"))
                .andExpect(jsonPath("$.result.count").value(1))
                .andExpect(jsonPath("$.result.data[0].title").value("백엔드 개발자"));
    }

    @Test
    @DisplayName("채용공고 상세 조회 성공")
    void getJobDetail_success() throws Exception {
        Job jobEntity = Job.builder()
                .id(1L)
                .companyName("카카오")
                .title("프론트엔드")
                .description("상세내용")
                .location("서울")
                .employmentType("정규직")
                .careerLevel("신입")
                .salary("4000만원")
                .deadline(LocalDate.now().plusDays(10))
                .sourceUrl("https://kakao.com/jobs/1234")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ResJobDto job = new ResJobDto(jobEntity);
        when(jobService.getJobDetail(1L)).thenReturn(job);

        mockMvc.perform(get("/api/jobs/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("채용공고 상세 조회 성공"))
                .andExpect(jsonPath("$.result.data.title").value("프론트엔드"));
    }
}
