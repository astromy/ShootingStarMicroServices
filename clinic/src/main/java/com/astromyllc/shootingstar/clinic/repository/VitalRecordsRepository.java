package com.astromyllc.shootingstar.clinic.repository;

import com.astromyllc.shootingstar.clinic.model.VitalRecords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VitalRecordsRepository extends JpaRepository<VitalRecords, Long> {

    List<VitalRecords> findByInstitutionCode(String institutionCode);

    List<VitalRecords> findByInstitutionCodeAndPatientId(String institutionCode, String patientId);
}