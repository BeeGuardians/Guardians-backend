package com.guardians.domain.wargame.repository;

import com.guardians.domain.user.entity.User;
import com.guardians.domain.wargame.entity.SolvedWargame;
import com.guardians.domain.wargame.entity.SolvedWargameId;
import com.guardians.domain.wargame.entity.Wargame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolvedWargameRepository extends JpaRepository<SolvedWargame, SolvedWargameId> {
    List<SolvedWargame> findAllByUserId(Long userId); // 추가
    boolean existsByUserIdAndWargameId(Long userId, Long wargameId);
    List<SolvedWargame> findByUser(User user);
    Long countByWargame(Wargame wargame);
    boolean existsByUserAndWargame(User user, Wargame wargame);
    List<SolvedWargame> findByWargame(Wargame wargame);
    List<SolvedWargame> findByUser_Id(Long userId);
    boolean existsByUser_IdAndWargame_Id(Long userId, Long wargameId);
}
