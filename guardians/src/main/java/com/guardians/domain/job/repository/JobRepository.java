package com.guardians.domain.job.repository;

import com.guardians.domain.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // 마감일 지나지 않았고, isActive=true인 공고들
    List<Job> findByIsActiveTrueAndDeadlineAfter(LocalDate now);

    // 상세 조회용
    Optional<Job> findByIdAndIsActiveTrue(Long id);

    // 관리자 페이지용 전체 조회
    List<Job> findAllByOrderByCreatedAtDesc();

    // 키워드 검색 (제목 + 회사명에 키워드 포함)
    List<Job> findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(String title, String companyName);
}