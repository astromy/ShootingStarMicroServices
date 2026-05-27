package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.PreOrderInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreOrderInstitutionRepository extends JpaRepository<PreOrderInstitution,Long> {
}
