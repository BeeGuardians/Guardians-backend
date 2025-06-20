package com.guardians.controller;

import com.guardians.domain.board.entity.BoardType;
import com.guardians.dto.board.req.ReqCreateBoardDto;
import com.guardians.dto.board.req.ReqUpdateBoardDto;
import com.guardians.dto.board.res.*;
import com.guardians.dto.common.ResWrapper;
import com.guardians.service.board.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            @RequestParam("type") BoardType boardType,
            @RequestBody @Valid ReqCreateBoardDto dto,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(ResWrapper.resError("유효성 검사 실패: " + errorMsg));
        }

        Long userId = (Long) session.getAttribute("userId");
        ResCreateBoardDto result = boardService.createBoard(userId, dto, boardType);
        return ResponseEntity.ok(ResWrapper.resSuccess("게시글이 성공적으로 등록되었습니다.", result));
    }

    // 게시글 목록 조회
    @Operation(summary = "게시글 목록 조회", description = "모든 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity<ResWrapper<?>> getBoardList(
            @RequestParam("type") BoardType boardType,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        if (keyword != null && keyword.trim().length() < 2) {
            return ResponseEntity.badRequest().body(
                    ResWrapper.resError("검색어는 2자 이상 입력해주세요.")
            );
        }
        List<ResBoardListDto> result = boardService.getBoardList(boardType, keyword);
        return ResponseEntity.ok(ResWrapper.resSuccess("게시글 목록 조회 성공", result));
    }

    @Operation(summary = "핫 게시글 조회", description = "좋아요 수와 조회수를 기반으로 상위 10개의 핫 게시글을 조회합니다.")
    @GetMapping("/hot")
    public ResponseEntity<ResWrapper<?>> getHotBoards() {
        List<ResHotBoardDto> result = boardService.getHotBoards();
        return ResponseEntity.ok(ResWrapper.resSuccess("핫 게시글 조회 성공", result));
    }

    // 게시글 상세 조회
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<ResWrapper<?>> getBoardDetail(@PathVariable Long boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        ResBoardDetailDto result = boardService.getBoardDetail(boardId, userId);
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
    //게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ResWrapper<?>> deleteBoard(
            HttpSession session,
            @PathVariable Long boardId
    ) {
        Long userId = (Long) session.getAttribute("userId");

        boardService.deleteBoard(userId, boardId);

        return ResponseEntity.ok(ResWrapper.resSuccess("게시글 삭제 완료", null));
    }

    // 게시글 좋아요 토글
    @PostMapping("/{boardId}/like")
    public ResponseEntity<ResWrapper<?>> toggleBoardLike(
            @PathVariable Long boardId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        boolean liked = boardService.toggleLike(userId, boardId);
        return ResponseEntity.ok(ResWrapper.resSuccess("게시글 좋아요 토글 완료",
                Map.of("liked", liked)
        ));
    }


}