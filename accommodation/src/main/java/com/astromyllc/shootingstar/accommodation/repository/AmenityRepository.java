package com.astromyllc.shootingstar.accommodation.repository;

import com.astromyllc.shootingstar.accommodation.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    List<Amenity> findByIdBlock(Long idBlock);

    List<Amenity> findByInstitutionCode(String institutionCode);
}
