package com.astromyllc.shootingstar.accommodation.repository;

import com.astromyllc.shootingstar.accommodation.model.DutyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DutyTypeRepository extends JpaRepository<DutyType, Long> {
    List<DutyType> findByInstitutionCode(String institutionCode);
}
