package com.astromyllc.shootingstar.clinic.repository;

import com.astromyllc.shootingstar.clinic.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByInstitutionCodeOrderByDateTimeDesc(String institutionCode);

    List<Inventory> findByMedicalProductIdOrderByDateTimeDesc(Long medicalProductId);
}