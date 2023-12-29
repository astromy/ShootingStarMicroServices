package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;

import java.util.List;
import java.util.Optional;

public interface ExamsAssessmentServiceInterface {
    public void submitExamsAssessment(ExamsAssessmentRequest examsAssessmentRequest);
    public void submitExamsAssessments(List<ExamsAssessmentRequest> examsAssessmentRequest);
    public List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByClass(ExamsAssessmentRequest examsAssessmentRequest);
    public List<Optional<ExamsAssessmentResponse>> getExamsAssessmentByStudent(ExamsAssessmentRequest examsAssessmentRequest);
    public List<List<Optional<ExamsAssessmentResponse>>> fetchPerformance(List<ExamsAssessmentRequest> examsAssessmentRequest);
    public List<List<Optional<ExamsAssessmentResponse>>> fetchStudentProgressionReport(List<ExamsAssessmentRequest> examsAssessmentRequest);
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerTerm(List<ExamsAssessmentRequest> examsAssessmentRequest);
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerTerm(List<ExamsAssessmentRequest> examsAssessmentRequest);
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForClassPerAcademicYear(List<ExamsAssessmentRequest> examsAssessmentRequest);
    public List<List<Optional<ExamsAssessmentResponse>>> fetchExamsAssessmentsForStudentPerAcademicYear(List<ExamsAssessmentRequest> examsAssessmentRequest);
}
