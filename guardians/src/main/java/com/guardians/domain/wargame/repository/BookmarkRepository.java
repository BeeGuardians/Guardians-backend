package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.Bookmark;
import com.guardians.domain.wargame.entity.BookmarkId;
import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {

    List<Bookmark> findByUser_Id(Long userId);

    List<Bookmark> findAllByUserId(Long userId); // 추가
    Optional<Bookmark> findByUserAndWargame(User user, Wargame wargame);
    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);

    boolean existsByUser_IdAndWargame_Id(Long userId, Long wargameId);
    void deleteByUser_IdAndWargame_Id(Long userId, Long wargameId);
}
