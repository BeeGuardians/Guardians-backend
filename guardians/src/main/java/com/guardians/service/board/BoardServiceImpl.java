package com.guardians.service.board;

import com.guardians.domain.board.entity.Board;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    public ResCreateBoardDto createBoard(Long userId, ReqCreateBoardDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
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
    public List<ResBoardListDto> getBoardList() {
        List<Board> boards = boardRepository.findAllWithUser();

        return boards.stream().map(board -> ResBoardListDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .username(board.getUser().getUsername())  // 이제 Lazy 문제 없음
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

        // 작성자 확인
        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 필드 수정
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
                .build();
    }

    @Override
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findByIdWithUser(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // 작성자만 삭제 가능
        if (!board.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        boardRepository.delete(board);
    }

}
