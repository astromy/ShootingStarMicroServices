package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.Announcement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnnouncementRepository extends MongoRepository<Announcement, String> {
    List<Announcement> findByInstitutionCodeOrderByTimestampDesc(String institutionCode);
}