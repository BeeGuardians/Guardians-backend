package com.guardians.service.board;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.BoardLike;
import com.guardians.domain.board.entity.BoardType;
import com.guardians.domain.board.repository.BoardLikeRepository;
import com.guardians.domain.board.repository.BoardRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.dto.board.req.ReqCreateBoardDto;
import com.guardians.dto.board.req.ReqUpdateBoardDto;
import com.guardians.dto.board.res.ResBoardDetailDto;
import com.guardians.dto.board.res.ResBoardListDto;
import com.guardians.dto.board.res.ResCreateBoardDto;
import com.guardians.dto.board.res.ResUpdateBoardDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final UserRepository userRepository;


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

        return boards.stream().map(board -> ResBoardListDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .username(board.getUser().getUsername())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .createdAt(board.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    @Override
    public ResBoardDetailDto getBoardDetail(Long boardId) {
        Board board = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        return ResBoardDetailDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .username(board.getUser().getUsername())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }

    @Override
    public ResUpdateBoardDto updateBoard(Long userId, Long boardId, ReqUpdateBoardDto dto) {
        Board board = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setBoardType(dto.getBoardType());
        board.setUpdatedAt(LocalDateTime.now());

        Board updatedBoard = boardRepository.save(board);

        return ResUpdateBoardDto.builder()
                .boardId(updatedBoard.getId())
                .title(updatedBoard.getTitle())
                .content(updatedBoard.getContent())
                .username(updatedBoard.getUser().getUsername())
                .updatedAt(updatedBoard.getUpdatedAt())
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
}
