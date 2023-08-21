package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.Dependants;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DependantRepository extends MongoRepository<Dependants,String> {
}
