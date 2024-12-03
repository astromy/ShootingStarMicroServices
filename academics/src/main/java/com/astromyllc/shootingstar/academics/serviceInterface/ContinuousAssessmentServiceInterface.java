package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ContinuousAssessmentResponse;
import com.astromyllc.shootingstar.academics.model.ContinuousAssessment;

import java.util.List;

public interface ContinuousAssessmentServiceInterface {
    public void submitContinuousAssessment(ContinuousAssessmentRequest continuousAssessmentRequest);
    public void submitContinuousAssessments(List<ContinuousAssessmentRequest> continuousAssessmentRequest);
    public List<ContinuousAssessmentResponse> getContinuousAssessmentByStudent(ContinuousAssessmentRequest continuousAssessmentRequest);
    public List<ContinuousAssessmentResponse> getContinuousAssessmentByClass(ContinuousAssessmentRequest continuousAssessmentRequest);

}
