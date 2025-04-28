package com.guardians.dto.mypage.res;

import com.guardians.domain.wargame.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ResReviewDto {

    private List<ReviewDto> reviews;

    @Getter
    @Builder
    public static class ReviewDto {
        private Long id;
        private String content;
        private String createdAt;
    }

    public static ResReviewDto fromEntities(List<Review> reviewEntities) {
        List<ReviewDto> reviewDtos = reviewEntities.stream()
                .map(review -> ReviewDto.builder()
                        .id(review.getId())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt().toString()) // LocalDateTime -> String 변환 예시
                        .build())
                .collect(Collectors.toList());

        return ResReviewDto.builder()
                .reviews(reviewDtos)
                .build();
    }
}
