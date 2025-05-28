package com.guardians.dto.wargame.req;

import com.guardians.domain.wargame.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCreateWargameDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Difficulty difficulty; // Enum 값 (EASY, MEDIUM, HARD 등)

    @NotNull
    private Integer score;

    @NotNull
    private Long categoryId; // FK - Category 엔티티

    @NotBlank
    private String dockerImageUrl;

    private String fileUrl; // 선택 업로드 파일이 있으면

    @NotBlank
    private String flag; // WargameFlag용
}
