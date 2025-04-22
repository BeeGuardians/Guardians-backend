package com.guardians.controller;

import com.guardians.dto.common.ResWrapper;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<ResWrapper<?>> createUser(@RequestBody @Valid ReqCreateUserDto requestDto) {
        ResCreateUserDto createdUser = userService.createUser(requestDto);
        System.out.println("zzz");
        return ResponseEntity.ok(ResWrapper.resSuccess(createdUser));
    }
}
