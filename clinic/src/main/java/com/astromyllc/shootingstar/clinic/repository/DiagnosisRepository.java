package com.astromyllc.shootingstar.clinic.repository;

import com.astromyllc.shootingstar.clinic.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    List<Diagnosis> findByInstitutionCode(String institutionCode);

    List<Diagnosis> findByInstitutionCodeAndPatientId(String institutionCode, String patientId);
}