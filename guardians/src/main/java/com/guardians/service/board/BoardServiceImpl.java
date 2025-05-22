package com.guardians.service.board;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.BoardLike;
import com.guardians.domain.board.entity.BoardType;
import com.guardians.domain.board.repository.BoardLikeRepository;
import com.guardians.domain.board.repository.BoardRepository;
import com.guardians.domain.board.repository.CommentCountRepository;
import com.guardians.domain.board.repository.CommentRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.dto.board.req.ReqCreateBoardDto;
import com.guardians.dto.board.req.ReqUpdateBoardDto;
import com.guardians.dto.board.res.*;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Override
    public ResCreateBoardDto createBoard(Long userId, ReqCreateBoardDto dto, BoardType boardType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .boardType(boardType)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Board saved = boardRepository.save(board);

        return ResCreateBoardDto.builder()
                .boardId(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .username(user.getUsername())
                .build();
    }

    @Override
    public List<ResBoardListDto> getBoardList(BoardType boardType) {
        List<Board> boards = boardRepository.findByBoardType(boardType);

        Map<Long, Long> commentCountMap = commentRepository.countCommentsByBoard().stream()
                .collect(Collectors.toMap(
                        projection -> projection.getBoardId(),
                        projection -> projection.getCommentCount()
                ));
        return boards.stream()
                .map(board -> ResBoardListDto.fromEntity(board)
                        .toBuilder()
                        .commentCount(commentCountMap.getOrDefault(board.getId(), 0L))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ResBoardListDto> getBoardList(BoardType boardType, String keyword) {
        List<Board> boards;

        if (keyword != null && !keyword.trim().isEmpty()) {
            boards = boardRepository.findByBoardTypeAndKeyword(boardType, keyword.trim());
        } else {
            boards = boardRepository.findByBoardType(boardType);
        }

        Map<Long, Long> commentCountMap = commentRepository.countCommentsByBoard().stream()
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
    @Override
    public ResBoardDetailDto getBoardDetail(Long boardId,Long userId) {
        Board board = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        boolean liked = boardLikeRepository.existsByBoardIdAndUserId(boardId, userId);

        board.increaseViewCount();

        return ResBoardDetailDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser().getUsername())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .userId(board.getUser().getId())
                .boardType(board.getBoardType().name())
                .liked(liked)
                .build();
    }
    @Transactional
    @Override
    public ResUpdateBoardDto updateBoard(Long userId, Long boardId, ReqUpdateBoardDto dto) {
        Board board = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setUpdatedAt(LocalDateTime.now());

        Board updatedBoard = boardRepository.save(board);

        return ResUpdateBoardDto.builder()
                .boardId(updatedBoard.getId())
                .title(updatedBoard.getTitle())
                .content(updatedBoard.getContent())
                .username(updatedBoard.getUser().getUsername())
                .updatedAt(updatedBoard.getUpdatedAt())
                .boardType(board.getBoardType().name())
                .build();
    }

    @Override
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        boardRepository.delete(board);
    }

    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long boardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Optional<BoardLike> existing = boardLikeRepository.findByUserIdAndBoardId(userId, boardId);

        if (existing.isPresent()) {
            boardLikeRepository.delete(existing.get());
            board.setLikeCount(board.getLikeCount() - 1);
            boardRepository.save(board);
            return false; // 좋아요 취소
        } else {
            BoardLike like = BoardLike.of(user, board);
            boardLikeRepository.save(like);
            board.setLikeCount(board.getLikeCount() + 1);
            boardRepository.save(board);
            return true; // 좋아요 등록
        }
    }

    @Transactional
    public void increaseViewCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
        board.increaseViewCount();
    }

    @Override
    public List<ResHotBoardDto> getHotBoards() {
        List<Board> allBoards = boardRepository.findAll();

        return allBoards.stream()
                .map(board -> new AbstractMap.SimpleEntry<>(board, board.getLikeCount() * 2 + board.getViewCount()))
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    Board board = entry.getKey();
                    int score = entry.getValue();
                    return ResHotBoardDto.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .boardType(board.getBoardType())
                            .likeCount(board.getLikeCount())
                            .viewCount(board.getViewCount())
                            .score(score)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
