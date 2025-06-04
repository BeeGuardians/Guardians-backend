package com.guardians.service.board;

import com.guardians.domain.board.entity.BoardType;
import com.guardians.dto.board.req.ReqCreateBoardDto;
import com.guardians.dto.board.req.ReqUpdateBoardDto;
import com.guardians.dto.board.res.*;

import java.util.List;

public interface BoardService {
    ResCreateBoardDto createBoard(Long userId, ReqCreateBoardDto dto, BoardType boardType);

    List<ResBoardListDto> getBoardList(BoardType boardType);

    List<ResBoardListDto> getBoardList(BoardType boardType, String keyword);

    ResBoardDetailDto getBoardDetail(Long boardId, Long userId);

    ResUpdateBoardDto updateBoard(Long userId, Long boardId, ReqUpdateBoardDto dto);

    void deleteBoard(Long userId, Long boardId);

    boolean toggleLike(Long userId, Long boardId);

    void increaseViewCount(Long boardId);

    List<ResHotBoardDto> getHotBoards();

}
