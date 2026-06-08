package com.guardians.domain.badge.port;

import com.guardians.domain.badge.entity.Badge;

import java.util.List;
import java.util.Optional;

public interface BadgePort {
    Optional<Badge> findByName(String name);
    List<Badge> findAll();
}
