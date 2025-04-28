package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.SolvedWargame;
import com.guardians.domain.wargame.entity.SolvedWargameId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolvedWargameRepository extends JpaRepository<SolvedWargame, SolvedWargameId> {
    List<SolvedWargame> findAllByUserId(Long userId); // 추가

}
