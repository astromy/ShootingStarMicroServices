package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.SalaryProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryProfileRepository extends JpaRepository<SalaryProfile, Long> {

    List<SalaryProfile> findByInstitutionCode(String institutionCode);

    Optional<SalaryProfile> findByInstitutionCodeAndStaffIdIgnoreCase(String institutionCode, String staffId);
}
