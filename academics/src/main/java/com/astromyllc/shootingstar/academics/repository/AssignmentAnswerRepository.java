package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.AssignmentAnswers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentAnswerRepository extends JpaRepository<AssignmentAnswers,Long> {
}
