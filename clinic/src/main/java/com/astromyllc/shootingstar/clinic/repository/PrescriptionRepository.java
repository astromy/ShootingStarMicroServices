package com.astromyllc.shootingstar.clinic.repository;

import com.astromyllc.shootingstar.clinic.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByInstitutionCode(String institutionCode);

    List<Prescription> findByInstitutionCodeAndPatientId(String institutionCode, String patientId);
}