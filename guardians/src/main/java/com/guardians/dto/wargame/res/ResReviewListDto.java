package com.guardians.dto.wargame.res;

import com.guardians.domain.wargame.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResReviewListDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private int likeCount;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userName;
    private Long wargameId;

    public static ResReviewListDto fromEntity(Review review) {
        return ResReviewListDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .likeCount(review.getLikeCount())
                .updatedAt(review.getUpdatedAt())
                .userId(review.getUser().getId())
                .userName(review.getUser().getUsername())
                .wargameId(review.getWargame().getId())
                .build();
    }
}
