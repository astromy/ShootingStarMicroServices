package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {
}