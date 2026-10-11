package com.astromyllc.shootingstar.accommodation.repository;

import com.astromyllc.shootingstar.accommodation.model.DutyAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DutyAssignmentRepository extends JpaRepository<DutyAssignment, Long> {
    List<DutyAssignment> findByIdBlock(Long idBlock);

    List<DutyAssignment> findByInstitutionCode(String institutionCode);

    List<DutyAssignment> findByRotationId(String rotationId);

    List<DutyAssignment> findByStudentIdAndInstitutionCode(String studentId, String institutionCode);
}
