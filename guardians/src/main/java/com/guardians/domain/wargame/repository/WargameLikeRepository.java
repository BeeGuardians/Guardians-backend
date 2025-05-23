package com.guardians.domain.wargame.repository;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.BookmarkId;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.entity.WargameLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface WargameLikeRepository extends JpaRepository<WargameLike, BookmarkId> {
    Optional<WargameLike> findByUserAndWargame(User user, Wargame wargame);

    @Query("SELECT wl.wargame.id FROM WargameLike wl WHERE wl.user.id = :userId")
    Set<Long> findWargameIdsByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);
}