package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.GeoCoordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeoCoordinateRepository extends JpaRepository<GeoCoordinate, Long> {
    List<GeoCoordinate> findByInstitution_Bececode(String bececode);

    // Deletes every stored point for one campus of one institution, directly
    // in the database. NULL/blank campus names count as "Main Campus", the
    // same rule as GeoCoordinateUtil.normalizeCampusName, so points saved
    // before multi-campus support are included.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from GeoCoordinate g " +
            "where g.institution.idInstitution = :institutionId " +
            "and lower(coalesce(nullif(trim(g.campusName), ''), 'Main Campus')) = lower(:campusName)")
    int deleteCampusPoints(@Param("institutionId") Long institutionId,
                           @Param("campusName") String campusName);
}