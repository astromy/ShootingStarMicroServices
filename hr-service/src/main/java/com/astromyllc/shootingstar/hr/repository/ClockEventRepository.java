package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.ClockEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ClockEventRepository extends MongoRepository<ClockEvent, String> {
    List<ClockEvent> findByStaffCodeOrderByTimestampDesc(String staffCode);

    // Between is exclusive at both ends, so callers pad the range by a day.
    List<ClockEvent> findByStaffCodeAndTimestampBetween(String staffCode, Instant from, Instant to);

    List<ClockEvent> findByInstitutionCodeAndTimestampBetween(String institutionCode, Instant from, Instant to);
}
