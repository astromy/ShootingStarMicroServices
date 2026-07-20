package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByInstitutionCode(String institutionCode);

    List<Assignment> findByInstitutionCodeAndClassId(String institutionCode, String classId);

    List<Assignment> findByInstitutionCodeAndStaffId(String institutionCode, String staffId);

    List<Assignment> findByInstitutionCodeAndClassIdAndSubjectId(String institutionCode, String classId, String subjectId);
}