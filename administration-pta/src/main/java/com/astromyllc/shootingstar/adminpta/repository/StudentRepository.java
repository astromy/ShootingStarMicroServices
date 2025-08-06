package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.Students;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends MongoRepository<Students, ObjectId> {
    @Query("SELECT s FROM Students s where s.status =:status")
    public List<Students> findStudentsByStatus(@Param("status")String status);
}
