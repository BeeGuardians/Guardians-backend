package com.guardians.service.board;

import com.guardians.dto.board.res.ResCommentListDto;
import com.guardians.dto.board.res.ResCreateCommentDto;
import com.guardians.dto.board.res.ResUpdateCommentDto;

import java.util.List;

public interface CommentService {
    ResCreateCommentDto createComment(Long userId, Long boardId, String content);

    List<ResCommentListDto> getCommentsByBoard(Long boardId);

    ResUpdateCommentDto updateComment(Long userId, Long commentId, String content);

    void deleteComment(Long userId, Long commentId);
}
