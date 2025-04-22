package com.guardians.service.user;

import com.guardians.domain.user.User;
import com.guardians.domain.user.UserRepository;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public ResCreateUserDto createUser(ReqCreateUserDto dto) {
        User user = dto.toEntity();
        User saved = userRepository.save(user);

        return ResCreateUserDto.fromEntity(saved);
    }
}
