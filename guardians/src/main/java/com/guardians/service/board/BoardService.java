package com.guardians.service.board;

import com.guardians.dto.board.req.ReqCreateBoardDto;
import com.guardians.dto.board.res.ResBoardDetailDto;
import com.guardians.dto.board.res.ResBoardListDto;
import com.guardians.dto.board.res.ResCreateBoardDto;

import java.util.List;

public interface BoardService {
    ResCreateBoardDto createBoard(Long userId, ReqCreateBoardDto dto);

    List<ResBoardListDto> getBoardList();

    ResBoardDetailDto getBoardDetail(Long boardId);
}
