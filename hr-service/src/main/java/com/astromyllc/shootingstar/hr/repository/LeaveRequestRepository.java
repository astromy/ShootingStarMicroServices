package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.LeaveRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeaveRequestRepository extends MongoRepository<LeaveRequest, String> {
    List<LeaveRequest> findByInstitutionCode(String institutionCode);

    List<LeaveRequest> findByInstitutionCodeAndStaffCode(String institutionCode, String staffCode);
}
