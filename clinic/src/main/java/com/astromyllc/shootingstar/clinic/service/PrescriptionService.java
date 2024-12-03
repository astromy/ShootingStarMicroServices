package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PrescriptionRequest;
import com.astromyllc.shootingstar.clinic.dto.response.PrescriptionResponse;
import com.astromyllc.shootingstar.clinic.serviceInterface.PrescriptionServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PrescriptionService implements PrescriptionServiceInterface {
    @Override
    public void insertPrescription(PrescriptionRequest prescriptionRequest) {

    }

    @Override
    public void insertPrescriptions(List<PrescriptionRequest> prescriptionRequestList) {

    }

    @Override
    public Optional<List<PrescriptionResponse>> fetchPrescriptionByPatient(PatientRequest patientRequest) {
        return Optional.empty();
    }

    @Override
    public Optional<List<PrescriptionResponse>> fetchPrescriptionByInstitution(PatientRequest patientRequest) {
        return Optional.empty();
    }
}
