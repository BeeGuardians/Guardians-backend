package com.guardians.service.board;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.Comment;
import com.guardians.domain.board.repository.BoardRepository;
import com.guardians.domain.board.repository.CommentRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.dto.board.res.ResCommentListDto;
import com.guardians.dto.board.res.ResCreateCommentDto;
import com.guardians.dto.board.res.ResUpdateCommentDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResCreateCommentDto createComment(Long userId, Long boardId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .content(content)
                .build();

        Comment saved = commentRepository.save(comment);

        return ResCreateCommentDto.fromEntity(saved);
    }


    @Override
    public List<ResCommentListDto> getCommentsByBoard(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardIdWithUser(boardId);

        return comments.stream()
                .map(ResCommentListDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResUpdateCommentDto updateComment(Long userId, Long commentId, String content) {
        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        comment.updateContent(content);

        return ResUpdateCommentDto.fromEntity(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        commentRepository.delete(comment);
    }

}
