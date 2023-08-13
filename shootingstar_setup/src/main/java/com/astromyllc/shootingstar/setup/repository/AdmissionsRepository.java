package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Admissions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionsRepository extends JpaRepository<Admissions,Long> {
}
