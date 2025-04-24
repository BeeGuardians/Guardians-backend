package com.guardians.domain.challenge.repository;

import com.guardians.domain.challenge.entity.Bookmark;
import com.guardians.domain.challenge.entity.BookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {

    List<Bookmark> findByUser_Id(Long userId);

    boolean existsByUser_IdAndChallenge_Id(Long userId, Long challengeId);

    void deleteByUser_IdAndChallenge_Id(Long userId, Long challengeId);
}
