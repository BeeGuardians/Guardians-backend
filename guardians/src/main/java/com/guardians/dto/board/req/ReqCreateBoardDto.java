package com.guardians.dto.board.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateBoardDto {
    private String title;
    private String content;
}
