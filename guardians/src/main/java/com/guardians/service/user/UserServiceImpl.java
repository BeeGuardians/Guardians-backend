package com.guardians.service.user;

import com.guardians.domain.user.User;
import com.guardians.domain.user.UserRepository;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        User user = User.create(dto.getUsername(), dto.getEmail(), dto.getPassword());
        User saved = userRepository.save(user);

        return ResCreateUserDto.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .build();
    }
}
