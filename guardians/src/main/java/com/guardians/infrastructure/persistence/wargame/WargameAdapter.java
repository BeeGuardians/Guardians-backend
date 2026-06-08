package com.guardians.infrastructure.persistence.wargame;

import com.guardians.domain.wargame.entity.Wargame;
import com.guardians.domain.wargame.port.WargamePort;
import com.guardians.dto.wargame.res.ResHotWargameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WargameAdapter implements WargamePort {

    private final JpaWargameRepository jpa;

    @Override
    public List<Wargame> findAllWithCategory() {
        return jpa.findAllWithCategory();
    }

    @Override
    public Optional<Wargame> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Wargame> findByIdWithCategory(Long id) {
        return jpa.findByIdWithCategory(id);
    }

    @Override
    public List<Wargame> findByCategoryName(String categoryName) {
        return jpa.findByCategoryName(categoryName);
    }

    @Override
    public Page<ResHotWargameDto> findHotWargames(Pageable pageable) {
        return jpa.findHotWargames(pageable);
    }

    @Override
    public Wargame save(Wargame wargame) {
        return jpa.save(wargame);
    }

    @Override
    public void delete(Wargame wargame) {
        jpa.delete(wargame);
    }
}
