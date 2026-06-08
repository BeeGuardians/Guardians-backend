package com.guardians.domain.wargame.port;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.entity.WargameLike;

import java.util.Optional;
import java.util.Set;

public interface WargameLikePort {
    Optional<WargameLike> findByUserAndWargame(User user, Wargame wargame);
    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);
    Set<Long> findWargameIdsByUserId(Long userId);
    WargameLike save(WargameLike wargameLike);
    void delete(WargameLike wargameLike);
}
