package com.guardians.service.board;

import com.guardians.dto.board.req.ReqCreateCommentDto;
import com.guardians.dto.board.req.ReqUpdateCommentDto;
import com.guardians.dto.board.res.ResCommentListDto;
import com.guardians.dto.board.res.ResCreateCommentDto;
import com.guardians.dto.board.res.ResUpdateCommentDto;

import java.util.List;

public interface CommentService {
    ResCreateCommentDto createComment(Long userId, Long boardId, ReqCreateCommentDto dto);

    List<ResCommentListDto> getCommentsByBoard(Long boardId);

    ResUpdateCommentDto updateComment(Long userId, Long commentId, ReqUpdateCommentDto dto);

    void deleteComment(Long userId, Long commentId);
}
