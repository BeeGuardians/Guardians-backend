package com.guardians.domain.wargame.repository;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.BookmarkId;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.entity.WargameLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WargameLikeRepository extends JpaRepository<WargameLike, BookmarkId> {
    Optional<WargameLike> findByUserAndWargame(User user, Wargame wargame);

    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);
}
