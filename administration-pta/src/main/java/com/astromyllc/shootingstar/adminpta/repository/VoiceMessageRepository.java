package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.VoiceMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface VoiceMessageRepository extends MongoRepository<VoiceMessage, String> {
    // Metadata only: the audio is left out by the database, so listing never loads it into memory.
    // The returned objects have no audioBase64 - they are for display, never for saving back.
    @Query(fields = "{ 'audioBase64' : 0 }")
    List<VoiceMessage> findByInstitutionCodeOrderByTimestampDesc(String institutionCode);
}