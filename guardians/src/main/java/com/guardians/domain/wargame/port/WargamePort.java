package com.guardians.domain.wargame.port;

import com.guardians.domain.wargame.entity.Wargame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WargamePort {
    List<Wargame> findAllWithCategory();
    Optional<Wargame> findById(Long id);
    Optional<Wargame> findByIdWithCategory(Long id);
    List<Wargame> findByCategoryName(String categoryName);
    Page<HotWargameResult> findHotWargames(Pageable pageable);
    Wargame save(Wargame wargame);
    void delete(Wargame wargame);
}
