package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.wargame.entity.Review;
import com.guardians.domain.wargame.port.ReviewPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewAdapter implements ReviewPort {

    private final JpaReviewRepository jpa;

    @Override
    public List<Review> findAllByWargameIdOrderByCreatedAtAsc(Long wargameId) {
        return jpa.findAllByWargameIdOrderByCreatedAtAsc(wargameId);
    }

    @Override
    public List<Review> findAllWithWargameByUserId(Long userId) {
        return jpa.findAllWithWargameByUserId(userId);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Review save(Review review) {
        return jpa.save(review);
    }

    @Override
    public void delete(Review review) {
        jpa.delete(review);
    }
}
