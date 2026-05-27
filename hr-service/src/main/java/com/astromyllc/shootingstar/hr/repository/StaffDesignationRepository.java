package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.StaffDesignation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StaffDesignationRepository extends MongoRepository<StaffDesignation,String> {
}
