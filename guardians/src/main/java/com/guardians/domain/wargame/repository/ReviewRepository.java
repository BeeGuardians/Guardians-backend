package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByUserId(Long userId);
    List<Review> findAllByWargameId(Long wargameId);
    List<Review> findAllByWargameIdOrderByCreatedAtAsc(Long wargameId);

    @Query("SELECT r FROM Review r JOIN FETCH r.wargame WHERE r.user.id = :userId")
    List<Review> findAllWithWargameByUserId(@Param("userId") Long userId);

}
