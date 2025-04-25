package com.guardians.controller;

import com.guardians.dto.board.req.ReqCreateBoardDto;
import com.guardians.dto.board.req.ReqUpdateBoardDto;
import com.guardians.dto.board.res.ResBoardDetailDto;
import com.guardians.dto.board.res.ResBoardListDto;
import com.guardians.dto.board.res.ResCreateBoardDto;
import com.guardians.dto.board.res.ResUpdateBoardDto;
import com.guardians.dto.common.ResWrapper;
import com.guardians.service.board.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Tag(name = "Board API", description = "자유게시판 관련 API")
public class BoardController {

    private final BoardService boardService;

    // 게시글 작성
    @Operation(summary = "게시글 작성", description = "세션의 userId와 작성 데이터를 이용하여 게시글을 등록합니다.")
    @PostMapping
    public ResponseEntity<ResWrapper<?>> createBoard(
            HttpSession session,
            @RequestBody @Valid ReqCreateBoardDto dto
    ) {
        Long userId = (Long) session.getAttribute("userId");
        ResCreateBoardDto result = boardService.createBoard(userId, dto);
        return ResponseEntity.ok(ResWrapper.resSuccess("게시글이 성공적으로 등록되었습니다.", result));
    }

    // 게시글 목록 조회
    @Operation(summary = "게시글 목록 조회", description = "모든 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity<ResWrapper<?>> getBoardList() {
        List<ResBoardListDto> result = boardService.getBoardList();
        return ResponseEntity.ok(ResWrapper.resSuccess("게시글 목록 조회 성공", result));
    }

    // 게시글 상세 조회
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<ResWrapper<?>> getBoardDetail(@PathVariable Long boardId) {
        ResBoardDetailDto result = boardService.getBoardDetail(boardId);
        return ResponseEntity.ok(ResWrapper.resSuccess("게시글 상세 조회 성공", result));
    }
    // 게시글 수정
    @Operation(summary = "게시글 수정", description = "게시글 작성자만 게시글을 수정할 수 있습니다.")
    @PatchMapping("/{boardId}")
    public ResponseEntity<ResWrapper<?>> updateBoard(
            HttpSession session,
            @PathVariable Long boardId,
            @RequestBody @Valid ReqUpdateBoardDto dto
    ) {
        Long userId = (Long) session.getAttribute("userId");

        ResUpdateBoardDto result = boardService.updateBoard(userId, boardId, dto);

        return ResponseEntity.ok(ResWrapper.resSuccess("게시글 수정 완료", result));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ResWrapper<?>> deleteBoard(
            HttpSession session,
            @PathVariable Long boardId
    ) {
        Long userId = (Long) session.getAttribute("userId");

        boardService.deleteBoard(userId, boardId);

        return ResponseEntity.ok(ResWrapper.resSuccess("게시글 삭제 완료", null));
    }






}