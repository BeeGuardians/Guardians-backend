package com.guardians.service.user;

import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;

public interface UserService {
    ResCreateUserDto createUser(ReqCreateUserDto dto);
}
