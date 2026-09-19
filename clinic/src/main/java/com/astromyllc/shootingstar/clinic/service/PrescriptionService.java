package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PrescriptionRequest;
import com.astromyllc.shootingstar.clinic.dto.response.PrescriptionResponse;
import com.astromyllc.shootingstar.clinic.model.Prescription;
import com.astromyllc.shootingstar.clinic.repository.PrescriptionRepository;
import com.astromyllc.shootingstar.clinic.serviceInterface.PrescriptionServiceInterface;
import com.astromyllc.shootingstar.clinic.util.PrescriptionUtil;
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
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionUtil prescriptionUtil;

    @Override
    public void insertPrescription(PrescriptionRequest prescriptionRequest) {
        Prescription prescription = prescriptionUtil.mapPrescriptionRequest_ToPrescription(prescriptionRequest);
        prescriptionRepository.save(prescription);
        PrescriptionUtil.prescriptionGlobalList.add(prescription);
    }

    @Override
    public void insertPrescriptions(List<PrescriptionRequest> prescriptionRequestList) {
        List<Prescription> prescriptions = prescriptionRequestList.stream()
                .map(prescriptionUtil::mapPrescriptionRequest_ToPrescription).toList();
        prescriptionRepository.saveAll(prescriptions);
        PrescriptionUtil.prescriptionGlobalList.addAll(prescriptions);
    }

    @Override
    public Optional<List<PrescriptionResponse>> fetchPrescriptionByPatient(PatientRequest patientRequest) {
        return Optional.of(PrescriptionUtil.prescriptionGlobalList.stream()
                .filter(p -> p.getPatientId().equalsIgnoreCase(patientRequest.getPatientId())
                        && p.getInstitutionCode().equalsIgnoreCase(patientRequest.getInstitutionCode()))
                .map(prescriptionUtil::mapPrescription_ToPrescriptionResponse).toList());
    }

    @Override
    public Optional<List<PrescriptionResponse>> fetchPrescriptionByInstitution(PatientRequest patientRequest) {
        return Optional.of(PrescriptionUtil.prescriptionGlobalList.stream()
                .filter(p -> p.getInstitutionCode().equalsIgnoreCase(patientRequest.getInstitutionCode()))
                .map(prescriptionUtil::mapPrescription_ToPrescriptionResponse).toList());
    }
}
