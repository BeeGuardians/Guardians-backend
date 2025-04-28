package com.guardians.dto.mypage.res;

import com.guardians.domain.wargame.entity.Bookmark;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResBookmarkDto {

    private List<BookmarkInfo> bookmarks;

    @Getter
    @Builder
    public static class BookmarkInfo {
        private Long wargameId;
        private String title;
    }

    public static ResBookmarkDto fromEntities(List<Bookmark> bookmarkList) {
        return ResBookmarkDto.builder()
                .bookmarks(
                        bookmarkList.stream()
                                .map(bookmark -> BookmarkInfo.builder()
                                        .wargameId(bookmark.getWargame().getId())
                                        .title(bookmark.getWargame().getTitle())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}

