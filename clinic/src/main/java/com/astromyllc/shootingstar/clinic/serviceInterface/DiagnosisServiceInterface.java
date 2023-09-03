package com.astromyllc.shootingstar.clinic.serviceInterface;

import com.astromyllc.shootingstar.clinic.dto.request.DiagnosisRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.response.DiagnosisResponse;

import java.util.List;
import java.util.Optional;

public interface DiagnosisServiceInterface {
    public void recordDiagnosis(DiagnosisRequest diagnosisRequest);
    public void recordDiagnosisList(List<DiagnosisRequest> diagnosisRequests);
    public Optional<List<DiagnosisResponse>> fetchDiagnosisByPatient(PatientRequest patientRequest);
    public Optional<List<DiagnosisResponse>> fetchDiagnosisByInstitution(PatientRequest patientRequest);
}
