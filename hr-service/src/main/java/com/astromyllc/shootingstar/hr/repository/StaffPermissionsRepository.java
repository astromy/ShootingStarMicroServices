package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.StaffPermissions;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StaffPermissionsRepository extends MongoRepository<StaffPermissions,String> {
}
