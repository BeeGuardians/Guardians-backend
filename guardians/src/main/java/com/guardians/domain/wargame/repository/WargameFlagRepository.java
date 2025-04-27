package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.WargameFlag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WargameFlagRepository extends JpaRepository<WargameFlag, Long> {
}
