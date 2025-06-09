package com.guardians.domain.wargame.repository;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Bookmark;
import com.guardians.domain.wargame.entity.BookmarkId;
import com.guardians.domain.wargame.entity.Wargame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    Optional<Bookmark> findByUserAndWargame(User user, Wargame wargame);
    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);

    @Query("SELECT b FROM Bookmark b JOIN FETCH b.wargame WHERE b.user.id = :userId")
    List<Bookmark> findAllWithWargameByUserId(@Param("userId") Long userId);

    @Query("SELECT b.wargame.id FROM Bookmark b WHERE b.user.id = :userId")
    Set<Long> findWargameIdsByUserId(@Param("userId") Long userId);

}
