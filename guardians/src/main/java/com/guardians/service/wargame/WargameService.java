package com.guardians.service.wargame;

import com.guardians.dto.wargame.res.ResSubmitFlagDto;
import com.guardians.dto.wargame.res.ResWargameListDto;

import java.util.List;

public interface WargameService {
    List<ResWargameListDto> getWargameList(Long userId);
    ResSubmitFlagDto submitFlag(Long userId, Long wargameId, String flag);

    boolean toggleBookmark(Long userId, Long wargameId);
    boolean toggleLike(Long userId, Long wargameId);

}
