package com.guardians.service.user;

import com.guardians.domain.user.User;
import com.guardians.domain.user.UserRepository;
import com.guardians.dto.user.req.ReqCreateUserDto;
import com.guardians.dto.user.req.ReqLoginDto;
import com.guardians.dto.user.res.ResCreateUserDto;
import com.guardians.dto.user.res.ResLoginDto;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // createUser 중복 검사 로직
    private void validateDuplicate(ReqCreateUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    @Transactional
    @Override
    public ResCreateUserDto createUser(ReqCreateUserDto dto) {
        // 중복 검사 호출
        validateDuplicate(dto);

        String encodedPw = passwordEncoder.encode(dto.getPassword());
        User user = User.create(dto.getUsername(), dto.getEmail(), encodedPw);
        User saved = userRepository.save(user);

        return ResCreateUserDto.fromEntity(saved);
    }


    @Transactional(readOnly = true)
    @Override
    public ResLoginDto login(ReqLoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 해싱된 비밀번호와 비교
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return ResLoginDto.fromEntity(user);
    }
}
