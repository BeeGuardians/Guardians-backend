package com.guardians.infrastructure.persistence.job;

import com.guardians.domain.job.entity.Job;
import com.guardians.domain.job.port.JobPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JobAdapter implements JobPort {

    private final JpaJobRepository jpa;

    @Override
    public Optional<Job> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Job> findByIdAndIsActiveTrue(Long id) {
        return jpa.findByIdAndIsActiveTrue(id);
    }

    @Override
    public List<Job> findByIsActiveTrueAndDeadlineAfter(LocalDate now) {
        return jpa.findByIsActiveTrueAndDeadlineAfter(now);
    }

    @Override
    public Job save(Job job) {
        return jpa.save(job);
    }

    @Override
    public void delete(Job job) {
        jpa.delete(job);
    }
}
