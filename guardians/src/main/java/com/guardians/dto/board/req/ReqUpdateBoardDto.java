package com.guardians.dto.board.req;

import com.guardians.domain.board.entity.BoardType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateBoardDto {
    private String title;
    private String content;
    private BoardType boardType;

}
