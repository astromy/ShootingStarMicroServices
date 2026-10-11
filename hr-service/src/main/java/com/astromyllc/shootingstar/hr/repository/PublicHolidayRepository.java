package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.PublicHoliday;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PublicHolidayRepository extends MongoRepository<PublicHoliday, String> {
    List<PublicHoliday> findByInstitutionCode(String institutionCode);

    List<PublicHoliday> findByInstitutionCodeAndYear(String institutionCode, int year);
}
