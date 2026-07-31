package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.PushToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PushTokenRepository extends MongoRepository<PushToken, String> {
    Optional<PushToken> findByStaffCode(String staffCode);

    List<PushToken> findByStaffCodeIn(List<String> staffCodes);
}