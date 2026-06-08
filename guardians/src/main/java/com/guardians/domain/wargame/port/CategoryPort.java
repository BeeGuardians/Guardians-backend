package com.guardians.domain.wargame.port;

import com.guardians.domain.wargame.entity.Category;

import java.util.Optional;

public interface CategoryPort {
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
}
