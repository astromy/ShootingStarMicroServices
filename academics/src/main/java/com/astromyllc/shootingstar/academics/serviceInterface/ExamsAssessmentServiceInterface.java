package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;

import java.util.List;
import java.util.Optional;

public interface ExamsAssessmentServiceInterface {
    void submitExamsAssessment(ExamsAssessmentRequest examsAssessmentRequest);
    void submitExamsAssessments(List<ExamsAssessmentRequest> examsAssessmentRequest);
    List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByClass(ExamsAssessmentRequest examsAssessmentRequest);
    List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByStudent(ExamsAssessmentRequest examsAssessmentRequest);
    List<List<Optional<ExamsAssessmentResponse>>> fetchPerformance(List<ExamsAssessmentRequest> examsAssessmentRequest);
    List<List<Optional<ExamsAssessmentResponse>>> fetchStudentProgressionReport(List<ExamsAssessmentRequest> examsAssessmentRequest);
    List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerTerm(List<ExamsAssessmentRequest> examsAssessmentRequest);
    List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerTerm(List<ExamsAssessmentRequest> examsAssessmentRequest);
    List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerAcademicYear(List<ExamsAssessmentRequest> examsAssessmentRequest);
    List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerAcademicYear(List<ExamsAssessmentRequest> examsAssessmentRequest);
}
