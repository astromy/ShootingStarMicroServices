package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StaffRepository extends MongoRepository<Staff,String> {
    // staffCode is @Indexed(unique = true) on Staff, but it isn't the
    // Mongo @Id (that's the ObjectId) - needed to look a staff member up
    // by the code the frontend actually has, e.g. for getPermissionsByStaff.
    Optional<Staff> findByStaffCode(String staffCode);
}
