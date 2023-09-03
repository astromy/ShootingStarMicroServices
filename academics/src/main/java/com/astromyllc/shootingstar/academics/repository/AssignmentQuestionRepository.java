package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.AssignmentQuestions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentQuestionRepository extends JpaRepository<AssignmentQuestions,Long> {
}
