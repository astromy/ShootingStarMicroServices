package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.SelectedExamQuestionAnswers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectedExamsQuestionAnswersRepository extends JpaRepository<SelectedExamQuestionAnswers,Long> {
}
