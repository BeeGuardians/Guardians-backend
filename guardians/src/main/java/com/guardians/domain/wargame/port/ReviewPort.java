package com.guardians.domain.wargame.port;

import com.guardians.domain.wargame.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewPort {
    List<Review> findAllByWargameIdOrderByCreatedAtAsc(Long wargameId);
    List<Review> findAllWithWargameByUserId(Long userId);
    Optional<Review> findById(Long id);
    Review save(Review review);
    void delete(Review review);
}
