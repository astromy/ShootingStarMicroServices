package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.StaffSubjects;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StaffSubjectsRepository extends MongoRepository<StaffSubjects,String> {
}
