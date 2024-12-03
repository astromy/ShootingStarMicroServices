package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Lookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LookUpRepository extends JpaRepository<Lookup,Long> {
}
