package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.Students;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Students, ObjectId> {
}
