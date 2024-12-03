package com.astromyllc.shootingstar.onlineapplication.repository;

import com.astromyllc.shootingstar.onlineapplication.model.Applications;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationsRepository extends MongoRepository<Applications, String> {
    @Query("{'':?0}")
    Optional<List<Applications>> findByCountry(String country);
    @Query("{'':?0}")
    Optional<List<Applications>>findByCity(String city);
    @Query("{'applicationInstitution':?0}")
    Optional<List<Applications>> findBySchool(String school);
    @Query("{'':?0}")
    Optional<List<Applications>> findByRegion(String region);
    @Query("{'applicationCode':?0}")
    Optional<Applications> findByCode(String code);
}
