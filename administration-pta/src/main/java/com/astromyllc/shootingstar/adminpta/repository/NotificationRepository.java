package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {

    List<Notification> findByInstitutionCodeOrderByTimestampDesc(String institutionCode);

    List<Notification> findByInstitutionCodeAndStudentIdOrderByTimestampDesc(String institutionCode, String studentId);

    Optional<Notification> findByIdAndInstitutionCode(ObjectId id, String institutionCode);
}
