package com.guardians.infrastructure.persistence.job;

import com.guardians.domain.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
interface JpaJobRepository extends JpaRepository<Job, Long> {
    List<Job> findByIsActiveTrueAndDeadlineAfter(LocalDate now);
    Optional<Job> findByIdAndIsActiveTrue(Long id);
}
