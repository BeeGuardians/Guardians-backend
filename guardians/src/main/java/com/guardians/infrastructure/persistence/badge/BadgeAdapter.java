package com.guardians.infrastructure.persistence.badge;

import com.guardians.domain.badge.entity.Badge;
import com.guardians.domain.badge.port.BadgePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BadgeAdapter implements BadgePort {

    private final JpaBadgeRepository jpa;

    @Override
    public Optional<Badge> findByName(String name) {
        return jpa.findByName(name);
    }

    @Override
    public List<Badge> findAll() {
        return jpa.findAll();
    }
}
