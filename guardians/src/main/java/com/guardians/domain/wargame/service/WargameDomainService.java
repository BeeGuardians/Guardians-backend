package com.guardians.domain.wargame.service;

import com.guardians.domain.wargame.entity.Bookmark;
import com.guardians.domain.wargame.entity.WargameLike;
import com.guardians.domain.wargame.port.BookmarkPort;
import com.guardians.domain.wargame.port.WargameLikePort;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.Wargame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WargameDomainService {

    private final BookmarkPort bookmarkPort;
    private final WargameLikePort wargameLikePort;

    /**
     * 북마크 토글: 존재하면 삭제(false), 없으면 생성(true)
     */
    public boolean toggleBookmark(User user, Wargame wargame) {
        Optional<Bookmark> existing = bookmarkPort.findByUserAndWargame(user, wargame);
        if (existing.isPresent()) {
            bookmarkPort.delete(existing.get());
            return false;
        } else {
            bookmarkPort.save(Bookmark.builder()
                    .user(user)
                    .wargame(wargame)
                    .createdAt(LocalDateTime.now())
                    .build());
            return true;
        }
    }

    /**
     * 좋아요 토글: 존재하면 삭제·likeCount 감소(false), 없으면 생성·likeCount 증가(true)
     */
    public boolean toggleLike(User user, Wargame wargame) {
        Optional<WargameLike> existing = wargameLikePort.findByUserAndWargame(user, wargame);
        if (existing.isPresent()) {
            wargameLikePort.delete(existing.get());
            wargame.decreaseLikeCount();
            return false;
        } else {
            wargameLikePort.save(WargameLike.builder()
                    .user(user)
                    .wargame(wargame)
                    .createdAt(LocalDateTime.now())
                    .build());
            wargame.increaseLikeCount();
            return true;
        }
    }

    /**
     * 플래그 정답 판별
     */
    public boolean isCorrectFlag(String storedFlag, String submittedFlag) {
        return storedFlag.equals(submittedFlag);
    }
}
