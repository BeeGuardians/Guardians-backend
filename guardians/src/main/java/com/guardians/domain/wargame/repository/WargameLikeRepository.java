package com.guardians.domain.wargame.repository;

import com.guardians.domain.wargame.entity.BookmarkId;
import com.guardians.domain.wargame.entity.WargameLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WargameLikeRepository extends JpaRepository<WargameLike, BookmarkId> {
}
