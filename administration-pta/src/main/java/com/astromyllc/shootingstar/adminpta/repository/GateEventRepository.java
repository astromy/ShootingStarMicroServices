package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.GateEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GateEventRepository extends MongoRepository<GateEvent, String> {
    List<GateEvent> findByStudentIdOrderByTimestampDesc(String studentId);
}
