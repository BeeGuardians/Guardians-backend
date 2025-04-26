package com.guardians.domain.wargame.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkId implements Serializable {

    private Long user;        // Bookmark.java의 필드명과 **일치**해야 함
    private Long wargame;   // 마찬가지로 필드명 기준임

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookmarkId)) return false;
        BookmarkId that = (BookmarkId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(wargame, that.wargame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, wargame);
    }
}
