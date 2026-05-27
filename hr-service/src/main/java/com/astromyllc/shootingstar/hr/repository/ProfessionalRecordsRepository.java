package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.ProfessionalRecords;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfessionalRecordsRepository extends MongoRepository<ProfessionalRecords,String> {
}
