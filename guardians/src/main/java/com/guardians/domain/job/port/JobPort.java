package com.guardians.domain.job.port;

import com.guardians.domain.job.entity.Job;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JobPort {
    Optional<Job> findById(Long id);
    Optional<Job> findByIdAndIsActiveTrue(Long id);
    List<Job> findByIsActiveTrueAndDeadlineAfter(LocalDate now);
    Job save(Job job);
    void delete(Job job);
}
