package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.WargameFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WargameFlagRepository extends JpaRepository<WargameFlag, Long> {
    Optional<WargameFlag> findByWargame_Id(Long wargameId);

}
