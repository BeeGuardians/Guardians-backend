package com.guardians.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 유저 찾기 (로그인용 등)
    Optional<User> findByEmail(String email);

    // username 중복 확인용
    boolean existsByUsername(String username);

    // email 중복 확인용
    boolean existsByEmail(String email);
}
