package com.guardians.dto.badge.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResUserBadgeDto {
    private Long id;
    private String name;
    private String description;
    private String trueIconUrl;
    private String falseIconUrl;
    private boolean earned;
}
