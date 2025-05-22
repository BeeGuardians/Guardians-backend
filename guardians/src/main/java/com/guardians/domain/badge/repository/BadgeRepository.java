package com.guardians.domain.badge.repository;

import com.guardians.domain.badge.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByName(String name);

}
