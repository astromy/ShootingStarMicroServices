package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.WorkHoursSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WorkHoursSettingsRepository extends MongoRepository<WorkHoursSettings, String> {
    Optional<WorkHoursSettings> findByInstitutionCode(String institutionCode);
}
