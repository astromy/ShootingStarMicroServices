package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment,Long> {
}
