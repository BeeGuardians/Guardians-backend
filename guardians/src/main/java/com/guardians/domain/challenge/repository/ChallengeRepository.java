package com.guardians.domain.challenge.repository;

import com.guardians.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByCategory_Id(Long categoryId);

    List<Challenge> findByTitleContaining(String keyword);
}
