package com.guardians.infrastructure.persistence.user;

import com.guardians.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmailOrUsername(String email, String username);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userStats WHERE u.id = :id")
    Optional<User> findWithStatsById(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userStats WHERE u.id IN :ids")
    List<User> findAllWithStatsByIdIn(@Param("ids") List<Long> ids);
}
