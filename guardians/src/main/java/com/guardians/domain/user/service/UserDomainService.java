package com.guardians.domain.user.service;

import com.guardians.domain.user.port.UserPort;
import com.guardians.exception.CustomException;
import com.guardians.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDomainService {

    private final UserPort userPort;

    /**
     * 이메일·사용자명 중복 검증
     */
    public void validateDuplicate(String email, String username) {
        if (userPort.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userPort.existsByUsername(username)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    /**
     * 본인 확인 검증 (sessionUserId == targetUserId)
     */
    public void validateSameUser(Long sessionUserId, Long targetUserId) {
        if (!sessionUserId.equals(targetUserId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }
}
