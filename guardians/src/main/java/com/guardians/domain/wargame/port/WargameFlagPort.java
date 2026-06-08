package com.guardians.domain.wargame.port;

import com.guardians.domain.wargame.entity.WargameFlag;

import java.util.List;
import java.util.Optional;

public interface WargameFlagPort {
    Optional<WargameFlag> findByWargameId(Long wargameId);
    List<WargameFlag> findAllByWargameIdIn(List<Long> wargameIds);
    WargameFlag save(WargameFlag wargameFlag);
}
