package com.guardians.domain.user.port;

import com.guardians.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserPort {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findWithStatsById(Long id);
    Optional<Long> findIdByEmail(String email);
    List<User> findAll();
    List<User> findAllWithStatsByIdIn(List<Long> ids);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmailOrUsername(String email, String username);
    User save(User user);
    void deleteById(Long id);
    void delete(User user);
}
