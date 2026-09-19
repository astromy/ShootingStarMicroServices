package com.astromyllc.shootingstar.clinic.repository;

import com.astromyllc.shootingstar.clinic.model.MedicalProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalProductRepository extends JpaRepository<MedicalProduct, Long> {

    List<MedicalProduct> findByInstitutionCodeAndActiveTrue(String institutionCode);

    Optional<MedicalProduct> findByInstitutionCodeAndProductCode(String institutionCode, String productCode);
}