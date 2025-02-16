package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ContinuousAssessmentResponse;

import java.util.List;
import java.util.Optional;

public interface ContinuousAssessmentServiceInterface {
    public Optional<ContinuousAssessmentResponse> submitContinuousAssessment(ContinuousAssessmentRequest continuousAssessmentRequest);
    public Optional<ContinuousAssessmentResponse> submitContinuousAssessments(List<ContinuousAssessmentRequest> continuousAssessmentRequest);
    public List<ContinuousAssessmentResponse> getContinuousAssessmentByStudent(ContinuousAssessmentRequest continuousAssessmentRequest);
    public List<ContinuousAssessmentResponse> getContinuousAssessmentByClass(ContinuousAssessmentRequest continuousAssessmentRequest);

}
