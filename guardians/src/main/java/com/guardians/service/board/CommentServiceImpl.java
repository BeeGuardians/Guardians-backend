package com.guardians.service.board;

import com.guardians.domain.board.entity.Board;
import com.guardians.domain.board.entity.Comment;
import com.guardians.domain.board.repository.BoardRepository;
import com.guardians.domain.board.repository.CommentRepository;
import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.repository.UserRepository;
import com.guardians.dto.board.req.ReqCreateCommentDto;
import com.guardians.dto.board.req.ReqUpdateCommentDto;
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
    public ResCreateCommentDto createComment(Long userId, Long boardId, ReqCreateCommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Comment comment = Comment.builder()
                .user(user)
                .board(board)
                .content(dto.getContent())
                .build();

        Comment saved = commentRepository.save(comment);

        return ResCreateCommentDto.builder()
                .commentId(saved.getId())
                .content(saved.getContent())
                .username(user.getUsername())
                .build();
    }


    @Override
    public List<ResCommentListDto> getCommentsByBoard(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardIdWithUser(boardId);

        return comments.stream().map(comment -> ResCommentListDto.builder()
                        .commentId(comment.getId())
                        .content(comment.getContent())
                        .username(comment.getUser().getUsername())
                        .profileImageUrl(comment.getUser().getProfileImageUrl())
                        .createdAt(comment.getCreatedAt())
                        .userId(comment.getUser().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResUpdateCommentDto updateComment(Long userId, Long commentId, ReqUpdateCommentDto dto) {
        Comment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        comment.updateContent(dto.getContent());

        return ResUpdateCommentDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getId())
                .build();
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
