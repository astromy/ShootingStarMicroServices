package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.DesignationList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DesignationListRepository extends MongoRepository<DesignationList,String> {
}
