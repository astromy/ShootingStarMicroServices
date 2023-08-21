package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PortfolioRepository extends MongoRepository<Portfolio,String> {
}
