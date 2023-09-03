package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.SelectedAssignmentQuestions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectedAssignmentQuestionsRepository extends JpaRepository<SelectedAssignmentQuestions,Long> {
}
