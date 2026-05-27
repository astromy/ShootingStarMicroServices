package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.AcademicRecords;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AcademicRecordsRepository extends MongoRepository<AcademicRecords,String> {
}
