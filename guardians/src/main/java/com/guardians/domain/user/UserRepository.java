package com.guardians.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 유저 찾기 (로그인용 등)
    Optional<User> findByEmail(String email);

    // username 중복 확인용
    boolean existsByUsername(String username);

    // email 중복 확인용
    boolean existsByEmail(String email);

    // username으로 조회 (로그인 외 기능들 예: 프로필 조회 등)
    Optional<User> findByUsername(String username);

    // email + username 중복 확인 (DB에서 동시에 확인하고 싶은 경우)
    boolean existsByEmailOrUsername(String email, String username);

}
