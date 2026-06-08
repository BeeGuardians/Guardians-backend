package com.guardians.infrastructure.persistence.user;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.user.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

    private final JpaUserRepository jpa;

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpa.findByUsername(username);
    }

    @Override
    public Optional<User> findWithStatsById(Long id) {
        return jpa.findWithStatsById(id);
    }

    @Override
    public Optional<Long> findIdByEmail(String email) {
        return jpa.findIdByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return jpa.findAll();
    }

    @Override
    public List<User> findAllWithStatsByIdIn(List<Long> ids) {
        return jpa.findAllWithStatsByIdIn(ids);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpa.existsByUsername(username);
    }

    @Override
    public boolean existsByEmailOrUsername(String email, String username) {
        return jpa.existsByEmailOrUsername(email, username);
    }

    @Override
    public User save(User user) {
        return jpa.save(user);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public void delete(User user) {
        jpa.delete(user);
    }
}
