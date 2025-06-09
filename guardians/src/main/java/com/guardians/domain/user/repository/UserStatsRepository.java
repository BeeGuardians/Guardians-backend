package com.guardians.domain.user.repository;

import com.guardians.domain.user.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {

    @Query("SELECT us FROM UserStats us JOIN FETCH us.user ORDER BY us.score DESC")
    List<UserStats> findAllWithUserOrderByScoreDesc();


    @Query("SELECT us FROM UserStats us JOIN FETCH us.user WHERE us.user.id = :id")
    Optional<UserStats> findWithUserById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserStats us SET us.totalSolved = :count WHERE us.user.id = :userId")
    void updateSolvedCount(@Param("userId") Long userId, @Param("count") Long count);

}
