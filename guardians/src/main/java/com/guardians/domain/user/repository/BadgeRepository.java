package com.guardians.domain.user.repository;

import com.guardians.domain.user.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
