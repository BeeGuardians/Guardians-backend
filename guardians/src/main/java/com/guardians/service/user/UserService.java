package com.guardians.service.user;

import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.req.ReqLoginDto;
import com.guardians.dto.user.req.ReqUpdateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    ResCreateUserDto createUser(ReqCreateUserDto dto);
    ResLoginDto login(ReqLoginDto dto);
    ResLoginDto updateUserInfo(Long sessionUserId, Long targetUserId, ReqUpdateUserDto dto);

}
