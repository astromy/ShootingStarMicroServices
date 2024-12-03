package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.SelectedExamQuestions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectedExamsQuestionsRepository extends JpaRepository<SelectedExamQuestions,Long> {
}
