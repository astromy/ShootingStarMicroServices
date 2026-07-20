package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.GeoCoordinate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeoCoordinateRepository extends JpaRepository<GeoCoordinate, Long> {
    List<GeoCoordinate> findByInstitution_Bececode(String bececode);
}