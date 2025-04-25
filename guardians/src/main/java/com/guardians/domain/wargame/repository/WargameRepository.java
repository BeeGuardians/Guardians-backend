package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.Wargame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WargameRepository extends JpaRepository<Wargame, Long> {

    List<Wargame> findByCategory_Id(Long categoryId);

    List<Wargame> findByTitleContaining(String keyword);
}
