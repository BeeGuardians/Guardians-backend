package com.guardians.domain.wargame.port;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Bookmark;
import com.guardians.domain.wargame.entity.Wargame;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookmarkPort {
    Optional<Bookmark> findByUserAndWargame(User user, Wargame wargame);
    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);
    Set<Long> findWargameIdsByUserId(Long userId);
    List<Bookmark> findAllWithWargameByUserId(Long userId);
    Bookmark save(Bookmark bookmark);
    void delete(Bookmark bookmark);
}
