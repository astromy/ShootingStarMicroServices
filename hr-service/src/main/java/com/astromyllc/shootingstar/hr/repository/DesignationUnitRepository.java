package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.DesignationUnit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DesignationUnitRepository extends MongoRepository<DesignationUnit,String> {
}
