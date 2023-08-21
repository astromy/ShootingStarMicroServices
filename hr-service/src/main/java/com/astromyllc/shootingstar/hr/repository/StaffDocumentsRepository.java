package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.StaffDocuments;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StaffDocumentsRepository extends MongoRepository<StaffDocuments,String> {
}
