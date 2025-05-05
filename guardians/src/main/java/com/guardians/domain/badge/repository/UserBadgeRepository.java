package com.guardians.domain.badge.repository;

import com.guardians.domain.badge.entity.UserBadge;
import com.guardians.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser(User user); // ← 이거 추가
}
