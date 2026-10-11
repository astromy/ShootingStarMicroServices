package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.HolidaySync;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HolidaySyncRepository extends MongoRepository<HolidaySync, String> {
    List<HolidaySync> findByYear(int year);
}
