package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.wargame.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaCategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
