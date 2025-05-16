package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUserId(Long userId);
    List<Review> findAllByWargameId(Long wargameId);
    List<Review> findAllByWargameIdOrderByCreatedAtAsc(Long wargameId);
}
