package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.NationalHoliday;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NationalHolidayRepository extends MongoRepository<NationalHoliday, String> {
    List<NationalHoliday> findByYear(int year);
}
