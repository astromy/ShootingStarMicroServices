package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation,Long> {
}
