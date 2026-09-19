package com.astromyllc.shootingstar.clinic.repository;

import com.astromyllc.shootingstar.clinic.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByInstitutionCodeOrderByCheckInTimeDesc(String institutionCode);

    List<Visit> findByInstitutionCodeAndStatusOrderByCheckInTimeDesc(String institutionCode, String status);

    List<Visit> findByInstitutionCodeAndPatientIdOrderByCheckInTimeDesc(String institutionCode, String patientId);

    /**
     * Used on startup / a retry endpoint to catch any visit whose parent-notification call previously failed.
     */
    List<Visit> findByParentNotifiedFalse();
}