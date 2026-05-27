package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InstitutionRepository extends JpaRepository<Institution,Long> {
}
