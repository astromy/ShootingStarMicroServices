package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.BusBoardingEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BusBoardingEventRepository extends MongoRepository<BusBoardingEvent, String> {
    List<BusBoardingEvent> findByStudentIdOrderByTimestampDesc(String studentId);
}