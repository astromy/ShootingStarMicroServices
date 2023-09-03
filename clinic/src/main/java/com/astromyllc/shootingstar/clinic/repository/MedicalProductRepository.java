package com.astromyllc.shootingstar.clinic.repository;

import com.astromyllc.shootingstar.clinic.model.MedicalProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalProductRepository extends JpaRepository<MedicalProduct,Long> {
}
