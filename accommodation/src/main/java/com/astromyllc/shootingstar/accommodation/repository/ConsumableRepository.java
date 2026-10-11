package com.astromyllc.shootingstar.accommodation.repository;

import com.astromyllc.shootingstar.accommodation.model.Consumable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumableRepository extends JpaRepository<Consumable, Long> {
    List<Consumable> findByIdBlock(Long idBlock);

    List<Consumable> findByInstitutionCode(String institutionCode);
}
