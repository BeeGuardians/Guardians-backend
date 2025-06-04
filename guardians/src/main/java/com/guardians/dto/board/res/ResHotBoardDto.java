package com.guardians.dto.board.res;

import com.guardians.domain.board.entity.BoardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResHotBoardDto {
    private Long id;
    private String title;
    private BoardType boardType;
    private int likeCount;
    private int viewCount;
    private int score;
}
