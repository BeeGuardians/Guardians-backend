package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Bookmark;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.port.BookmarkPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BookmarkAdapter implements BookmarkPort {

    private final JpaBookmarkRepository jpa;

    @Override
    public Optional<Bookmark> findByUserAndWargame(User user, Wargame wargame) {
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
    public List<Bookmark> findAllWithWargameByUserId(Long userId) {
        return jpa.findAllWithWargameByUserId(userId);
    }

    @Override
    public Bookmark save(Bookmark bookmark) {
        return jpa.save(bookmark);
    }

    @Override
    public void delete(Bookmark bookmark) {
        jpa.delete(bookmark);
    }
}
