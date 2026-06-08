package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.entity.WargameLike;
import com.guardians.domain.wargame.port.WargameLikePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class WargameLikeAdapter implements WargameLikePort {

    private final JpaWargameLikeRepository jpa;

    @Override
    public Optional<WargameLike> findByUserAndWargame(User user, Wargame wargame) {
        return jpa.findByUserAndWargame(user, wargame);
    }

    @Override
    public boolean existsByUserIdAndWargameId(Long userId, Long wargameId) {
        return jpa.existsByUserIdAndWargameId(userId, wargameId);
    }

    @Override
    public Set<Long> findWargameIdsByUserId(Long userId) {
        return jpa.findWargameIdsByUserId(userId);
    }

    @Override
    public WargameLike save(WargameLike wargameLike) {
        return jpa.save(wargameLike);
    }

    @Override
    public void delete(WargameLike wargameLike) {
        jpa.delete(wargameLike);
    }
}
