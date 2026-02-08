package com.guardians.service.board;

import com.guardians.domain.board.entity.BoardType;
import com.guardians.dto.board.res.*;

import java.util.List;

public interface BoardService {
    ResCreateBoardDto createBoard(Long userId, String title, String content, BoardType boardType);

    List<ResBoardListDto> getBoardList(BoardType boardType);

    List<ResBoardListDto> getBoardList(BoardType boardType, String keyword);

    ResBoardDetailDto getBoardDetail(Long boardId, Long userId);

    ResUpdateBoardDto updateBoard(Long userId, Long boardId, String title, String content);

    void deleteBoard(Long userId, Long boardId);

    boolean toggleLike(Long userId, Long boardId);

    void increaseViewCount(Long boardId);

    List<ResHotBoardDto> getHotBoards();

}
