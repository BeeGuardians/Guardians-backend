package com.guardians.dto.wargame.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodStatusDto {
    private String status;
    private String url;
}
