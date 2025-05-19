package com.guardians.domain.user.repository;

import com.guardians.domain.user.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {

    // 유저 + 유저 통계 데이터를 함께 가져오는 정렬된 쿼리
    // find all with user -> order by score desc
    @Query("SELECT us FROM UserStats us JOIN FETCH us.user ORDER BY us.score DESC")
    List<UserStats> findAllWithUserOrderByScoreDesc();
    Optional<UserStats> findByUserId(Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserStats us SET us.totalSolved = :count WHERE us.user.id = :userId")
    void updateSolvedCount(@Param("userId") Long userId, @Param("count") Long count);

}
