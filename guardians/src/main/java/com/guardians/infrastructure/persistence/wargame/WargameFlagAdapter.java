package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.wargame.entity.WargameFlag;
import com.guardians.domain.wargame.port.WargameFlagPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WargameFlagAdapter implements WargameFlagPort {

    private final JpaWargameFlagRepository jpa;

    @Override
    public Optional<WargameFlag> findByWargameId(Long wargameId) {
        return jpa.findByWargame_Id(wargameId);
    }

    @Override
    public List<WargameFlag> findAllByWargameIdIn(List<Long> wargameIds) {
        return jpa.findAllByWargameIdIn(wargameIds);
    }

    @Override
    public WargameFlag save(WargameFlag wargameFlag) {
        return jpa.save(wargameFlag);
    }
}
