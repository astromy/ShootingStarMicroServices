package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.Attendance;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<Attendance, ObjectId> {

    Optional<Attendance> findByStudentIdAndDate(String studentId, String date);

    // Inclusive on both ends (derived "Between" queries are exclusive in Mongo).
    @Query("{ 'institutionCode': ?0, 'date': { $gte: ?1, $lte: ?2 } }")
    List<Attendance> findByInstitutionAndDateRange(String institutionCode, String dateFrom, String dateTo);
}
