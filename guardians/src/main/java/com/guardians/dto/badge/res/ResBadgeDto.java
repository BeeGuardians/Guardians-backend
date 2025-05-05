package com.guardians.dto.badge.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResBadgeDto {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private boolean earned;
}
