package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.wargame.entity.WargameFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface JpaWargameFlagRepository extends JpaRepository<WargameFlag, Long> {
    Optional<WargameFlag> findByWargame_Id(Long wargameId);

    @Query("SELECT wf FROM WargameFlag wf WHERE wf.wargame.id IN :ids")
    List<WargameFlag> findAllByWargameIdIn(@Param("ids") List<Long> ids);
}
