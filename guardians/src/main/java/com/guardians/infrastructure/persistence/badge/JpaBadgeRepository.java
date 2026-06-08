package com.guardians.infrastructure.persistence.badge;

import com.guardians.domain.badge.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaBadgeRepository extends JpaRepository<Badge, Long> {
    Optional<Badge> findByName(String name);
}
