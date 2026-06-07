package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.SalarySettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalarySettingsRepository extends JpaRepository<SalarySettings, Long> {
    Optional<SalarySettings> findByInstitutionCode(String institutionCode);
}