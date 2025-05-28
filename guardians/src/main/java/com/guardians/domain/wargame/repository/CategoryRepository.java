package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Optional: 카테고리 이름으로 찾기 (중복 방지용)
    Optional<Category> findByName(String name);

}
