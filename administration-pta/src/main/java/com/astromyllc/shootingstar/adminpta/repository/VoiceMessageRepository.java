package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.VoiceMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VoiceMessageRepository extends MongoRepository<VoiceMessage, String> {
    List<VoiceMessage> findByInstitutionCodeOrderByTimestampDesc(String institutionCode);
}