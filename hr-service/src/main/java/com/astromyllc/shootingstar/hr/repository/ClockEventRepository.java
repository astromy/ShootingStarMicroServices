package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.ClockEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClockEventRepository extends MongoRepository<ClockEvent, String> {
    List<ClockEvent> findByStaffCodeOrderByTimestampDesc(String staffCode);
}