package com.guardians.controller;

import com.guardians.dto.board.req.ReqCreateCommentDto;
import com.guardians.dto.board.req.ReqUpdateCommentDto;
import com.guardians.dto.board.res.ResCommentListDto;
import com.guardians.dto.board.res.ResCreateCommentDto;
import com.guardians.dto.board.res.ResUpdateCommentDto;
import com.guardians.dto.common.ResWrapper;
import com.guardians.service.board.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
@Tag(name = "Comment API", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ResWrapper<?>> createComment(
            @PathVariable Long boardId,
            HttpSession session,
            @RequestBody @Valid ReqCreateCommentDto dto
    ) {
        Long userId = (Long) session.getAttribute("userId");
        ResCreateCommentDto result = commentService.createComment(userId, boardId, dto);

        return ResponseEntity.ok(ResWrapper.resSuccess("댓글 작성 완료", result));
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글에 달린 댓글들을 조회합니다.")
    @GetMapping
    public ResponseEntity<ResWrapper<?>> getComment(
            @PathVariable Long boardId
    ) {

        List<ResCommentListDto> result = commentService.getCommentsByBoard(boardId);
        return ResponseEntity.ok(ResWrapper.resSuccess("댓글 조회 성공", result));
    }

    @Operation(summary = "댓글 수정", description = "게시글에 달린 댓글들을 수정합니다.")
    @PatchMapping("/{commentId}")
    public ResponseEntity<ResWrapper<?>> updateComment(

            @PathVariable Long boardId,
            @PathVariable Long commentId,
            HttpSession session,
            @RequestBody @Valid ReqUpdateCommentDto dto
    ) {
        Long userId = (Long) session.getAttribute("userId");
        ResUpdateCommentDto result = commentService.updateComment(userId, commentId, dto);
        return ResponseEntity.ok(ResWrapper.resSuccess("댓글 수정 완료", result));

    }

    //댓글 삭제
    @Operation(summary = "댓글 삭제", description = "게시글에 달린 댓글을 삭제합니다")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResWrapper<?>> deleteComment(
            HttpSession session,
            @PathVariable Long boardId,
            @PathVariable Long commentId
    ){
        Long userId = (Long) session.getAttribute("userId");
        commentService.deleteComment(userId,commentId);
        return ResponseEntity.ok(ResWrapper.resSuccess("댓글 삭제 완료", null));
    }



}