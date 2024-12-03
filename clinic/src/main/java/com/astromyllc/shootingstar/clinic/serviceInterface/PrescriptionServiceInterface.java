package com.astromyllc.shootingstar.clinic.serviceInterface;

import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PrescriptionRequest;
import com.astromyllc.shootingstar.clinic.dto.response.PrescriptionResponse;

import java.util.List;
import java.util.Optional;

public interface PrescriptionServiceInterface {
    public void insertPrescription(PrescriptionRequest prescriptionRequest);
    public void insertPrescriptions(List<PrescriptionRequest> prescriptionRequestList);
    public Optional<List<PrescriptionResponse>> fetchPrescriptionByPatient(PatientRequest patientRequest);
    public Optional<List<PrescriptionResponse>> fetchPrescriptionByInstitution(PatientRequest patientRequest);
}
