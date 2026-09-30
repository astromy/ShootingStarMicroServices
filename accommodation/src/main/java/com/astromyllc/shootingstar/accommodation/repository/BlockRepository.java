package com.astromyllc.shootingstar.accommodation.repository;

import com.astromyllc.shootingstar.accommodation.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByInstitutionCode(String institutionCode);

    Optional<Block> findByIdBlockAndInstitutionCode(Long idBlock, String institutionCode);
}
