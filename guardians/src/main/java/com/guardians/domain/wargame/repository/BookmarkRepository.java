package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.Bookmark;
import com.guardians.domain.wargame.entity.BookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {

    List<Bookmark> findByUser_Id(Long userId);

    boolean existsByUser_IdAndWargame_Id(Long userId, Long wargameId);

    void deleteByUser_IdAndWargame_Id(Long userId, Long wargameId);
}
