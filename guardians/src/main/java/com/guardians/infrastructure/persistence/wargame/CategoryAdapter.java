package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.wargame.entity.Category;
import com.guardians.domain.wargame.port.CategoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryAdapter implements CategoryPort {

    private final JpaCategoryRepository jpa;

    @Override
    public Optional<Category> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpa.findByName(name);
    }
}
