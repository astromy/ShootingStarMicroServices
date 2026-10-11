package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.LeaveEntitlement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeaveEntitlementRepository extends MongoRepository<LeaveEntitlement, String> {
    List<LeaveEntitlement> findByInstitutionCode(String institutionCode);
}
