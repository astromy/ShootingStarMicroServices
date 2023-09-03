package com.astromyllc.shootingstar.academics.serviceInterface;

import com.astromyllc.shootingstar.academics.dto.request.ContinuousAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.request.ExamsAssessmentRequest;
import com.astromyllc.shootingstar.academics.dto.response.ExamsAssessmentResponse;

import java.util.List;

public interface ExamsAssessmentServiceInterface {
    public void submitExamsAssessment(ExamsAssessmentRequest examsAssessmentRequest);
    public void submitExamsAssessments(List<ExamsAssessmentRequest> examsAssessmentRequest);
    public List<ExamsAssessmentResponse> getExamsAssessmentByStudent(ExamsAssessmentRequest examsAssessmentRequest);
    public List<ExamsAssessmentResponse> getExamsAssessmentByClass(ExamsAssessmentRequest examsAssessmentRequest);


}
