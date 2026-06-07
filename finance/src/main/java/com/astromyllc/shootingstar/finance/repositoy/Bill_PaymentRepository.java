package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.Bill_Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Bill_PaymentRepository extends JpaRepository<Bill_Payment, Long> {
    List<Bill_Payment> findByInstitutionCode(String institutionCode);

    List<Bill_Payment> findByStudentIdAndInstitutionCode(String studentId, String institutionCode);

    List<Bill_Payment> findByStudentIdAndInstitutionCodeAndTermAndAcademicYear(
            String studentId, String institutionCode, String term, String academicYear);
}