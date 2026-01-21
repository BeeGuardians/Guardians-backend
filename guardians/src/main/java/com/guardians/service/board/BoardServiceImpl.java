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
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Transactional
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

    @Transactional
    @Override
    public List<ResBoardListDto> getBoardList(BoardType boardType) {
        return getBoardList(boardType, null);
    }

    @Transactional
    @Override
    public List<ResBoardListDto> getBoardList(BoardType boardType, String keyword) {
        List<Board> boards = (keyword != null && !keyword.trim().isEmpty())
                ? boardRepository.findByBoardTypeAndKeyword(boardType, keyword.trim())
                : boardRepository.findByBoardType(boardType);

        return toBoardListDtos(boards);
    }

    @Transactional
    private List<ResBoardListDto> toBoardListDtos(List<Board> boards) {
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

	board.update(dto.getTitle(), dto.getContent());

        return ResUpdateBoardDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser().getUsername())
                .updatedAt(board.getUpdatedAt())
                .boardType(board.getBoardType().name())
                .build();
    }

    @Transactional
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
            board.decreaseLikeCount();
            return false; // 좋아요 취소
        } else {
            BoardLike like = BoardLike.of(user, board);
            boardLikeRepository.save(like);
            board.increaseLikeCount();
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
    @Transactional(readOnly = true)
    public List<ResHotBoardDto> getHotBoards() {
        // DB에서 점수 계산 후 상위 10개만 조회 (N+1 해결)
        List<Board> hotBoards = boardRepository.findTop10ByHotScore();

        return hotBoards.stream()
                .map(board -> ResHotBoardDto.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .boardType(board.getBoardType())
                        .likeCount(board.getLikeCount())
                        .viewCount(board.getViewCount())
                        .score(board.getLikeCount() * 2 + board.getViewCount())
                        .build())
                .collect(Collectors.toList());
    }
}
