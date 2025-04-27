package com.guardians.domain.user.repository;

import com.guardians.domain.user.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
}
