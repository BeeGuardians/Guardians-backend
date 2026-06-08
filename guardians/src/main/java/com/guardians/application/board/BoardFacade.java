package com.guardians.application.board;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.BoardLike;
import com.guardians.domain.board.entity.BoardType;
import com.guardians.domain.board.entity.Comment;
import com.guardians.domain.board.port.BoardLikePort;
import com.guardians.domain.board.port.BoardPort;
import com.guardians.domain.board.port.CommentPort;
import com.guardians.domain.board.port.CommentCountRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.port.UserPort;
import com.guardians.dto.board.res.*;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardFacade {

    private final BoardPort boardPort;
    private final BoardLikePort boardLikePort;
    private final CommentPort commentPort;
    private final UserPort userPort;

    @Transactional
    public ResCreateBoardDto createBoard(Long userId, String title, String content, BoardType boardType) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = Board.builder()
                .title(title)
                .content(content)
                .boardType(boardType)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Board saved = boardPort.save(board);
        return ResCreateBoardDto.fromEntity(saved);
    }

    @Transactional
    public List<ResBoardListDto> getBoardList(BoardType boardType) {
        return getBoardList(boardType, null);
    }

    @Transactional
    public List<ResBoardListDto> getBoardList(BoardType boardType, String keyword) {
        List<Board> boards = (keyword != null && !keyword.trim().isEmpty())
                ? boardPort.findByBoardTypeAndKeyword(boardType, keyword.trim())
                : boardPort.findByBoardType(boardType);

        return toBoardListDtos(boards);
    }

    @Transactional
    private List<ResBoardListDto> toBoardListDtos(List<Board> boards) {
        Map<Long, Long> commentCountMap = commentPort.countCommentsByBoard().stream()
                .collect(Collectors.toMap(
                        CommentCountRepository::getBoardId,
                        CommentCountRepository::getCommentCount
                ));

        return boards.stream()
                .map(board -> ResBoardListDto.fromEntity(board)
                        .toBuilder()
                        .commentCount(commentCountMap.getOrDefault(board.getId(), 0L))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ResBoardDetailDto getBoardDetail(Long boardId, Long userId) {
        Board board = boardPort.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        boolean liked = boardLikePort.existsByBoardIdAndUserId(boardId, userId);
        board.increaseViewCount();

        return ResBoardDetailDto.fromEntity(board, liked);
    }

    @Transactional
    public ResUpdateBoardDto updateBoard(Long userId, Long boardId, String title, String content) {
        Board board = boardPort.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        board.update(title, content);
        return ResUpdateBoardDto.fromEntity(board);
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardPort.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        boardPort.delete(board);
    }

    @Transactional
    public boolean toggleLike(Long userId, Long boardId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = boardPort.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Optional<BoardLike> existing = boardLikePort.findByUserIdAndBoardId(userId, boardId);

        if (existing.isPresent()) {
            boardLikePort.delete(existing.get());
            board.decreaseLikeCount();
            return false;
        } else {
            boardLikePort.save(BoardLike.of(user, board));
            board.increaseLikeCount();
            return true;
        }
    }

    @Transactional
    public void increaseViewCount(Long boardId) {
        Board board = boardPort.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        board.increaseViewCount();
    }

    public List<ResHotBoardDto> getHotBoards() {
        return boardPort.findTop10ByHotScore().stream()
                .map(ResHotBoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Comment operations

    @Transactional
    public ResCreateCommentDto createComment(Long userId, Long boardId, String content) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = boardPort.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .content(content)
                .build();

        return ResCreateCommentDto.fromEntity(commentPort.save(comment));
    }

    public List<ResCommentListDto> getCommentsByBoard(Long boardId) {
        return commentPort.findByBoardIdWithUser(boardId).stream()
                .map(ResCommentListDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResUpdateCommentDto updateComment(Long userId, Long commentId, String content) {
        Comment comment = commentPort.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        comment.updateContent(content);
        return ResUpdateCommentDto.fromEntity(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentPort.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        commentPort.delete(comment);
    }
}
