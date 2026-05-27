package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StaffRepository extends MongoRepository<Staff,String> {
}
