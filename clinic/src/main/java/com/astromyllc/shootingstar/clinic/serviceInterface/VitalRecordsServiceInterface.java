package com.astromyllc.shootingstar.clinic.serviceInterface;

import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VitalRecordsRequest;
import com.astromyllc.shootingstar.clinic.dto.response.VitalRecordsResponse;

import java.util.List;

public interface VitalRecordsServiceInterface {
    public void recordVital(VitalRecordsRequest vitalRecordsRequest);
    public void recordVitals(List<VitalRecordsRequest> vitalRecordsRequestList);
    public VitalRecordsResponse fetchVitalRecordsByPatient(PatientRequest patientRequest);
    public List<VitalRecordsResponse> fetchVitalRecordsByInstitution(PatientRequest patientRequest);

}
