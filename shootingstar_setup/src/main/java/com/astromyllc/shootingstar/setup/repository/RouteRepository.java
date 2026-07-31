package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}