package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.Salaries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalariesRepository extends JpaRepository<Salaries, Long> {
    List<Salaries> findByInstitutionCode(String institutionCode);

    List<Salaries> findByInstitutionCodeAndAcademicYear(String institutionCode, String academicYear);

    List<Salaries> findByInstitutionCodeAndAcademicYearAndPayPeriod(
            String institutionCode, String academicYear, String payPeriod);

    List<Salaries> findByStaffIdAndInstitutionCode(String staffId, String institutionCode);
}