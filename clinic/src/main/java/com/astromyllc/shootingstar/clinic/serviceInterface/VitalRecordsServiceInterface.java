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

    // Additive — fetchVitalRecordsByPatient above only returns the single
    // most recent record (that's the existing contract, left untouched).
    // A patient-history screen needs the full timeline, hence this.
    public List<VitalRecordsResponse> fetchAllVitalRecordsByPatient(PatientRequest patientRequest);
}
