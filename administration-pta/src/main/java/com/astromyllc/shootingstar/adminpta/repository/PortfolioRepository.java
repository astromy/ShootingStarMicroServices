package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.Portfolio;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PortfolioRepository extends MongoRepository<Portfolio, ObjectId> {
}
