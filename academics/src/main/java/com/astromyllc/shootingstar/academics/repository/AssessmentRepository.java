package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment,Long> {

    Optional<Assessment> findByStudentIdAndSubjectAndTermAndAcademicYearAndInstitutionCode(
            String studentId, String subject, String term, String academicYear, String institutionCode);
}
