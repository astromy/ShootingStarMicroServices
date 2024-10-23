package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.dto.request.DiagnosisRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.response.DiagnosisResponse;
import com.astromyllc.shootingstar.clinic.model.Diagnosis;
import com.astromyllc.shootingstar.clinic.repository.DiagnosisRepository;
import com.astromyllc.shootingstar.clinic.serviceInterface.DiagnosisServiceInterface;
import com.astromyllc.shootingstar.clinic.util.DiagnosisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiagnosisService implements DiagnosisServiceInterface {
    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisUtil diagnosisUtil;
    @Override
    public void recordDiagnosis(DiagnosisRequest diagnosisRequest) {
        Diagnosis diagnosis=diagnosisUtil.mapDiagnososRequest_ToDiagnosis(diagnosisRequest);
        diagnosisRepository.save(diagnosis);
        diagnosisUtil.diagnosisGlobalList.add(diagnosis);
    }

    @Override
    public void recordDiagnosisList(List<DiagnosisRequest> diagnosisRequests) {
        List<Diagnosis> diagnosis=diagnosisRequests.stream().map(d->diagnosisUtil.mapDiagnososRequest_ToDiagnosis(d)).collect(Collectors.toList());
        diagnosisRepository.saveAll(diagnosis);
        diagnosisUtil.diagnosisGlobalList.addAll(diagnosis);
    }

    @Override
    public Optional<List<DiagnosisResponse>> fetchDiagnosisByPatient(PatientRequest patientRequest) {
        Optional<List<DiagnosisResponse>>  diagnosis= Optional.of(diagnosisUtil.diagnosisGlobalList.stream()
                .filter(diagnosis1 -> diagnosis1.getPatientId().equalsIgnoreCase(patientRequest.getPatientId())
                        && diagnosis1.getInstitutionCode().equalsIgnoreCase(patientRequest.getInstitutionCode())).collect(Collectors.toList())
                .stream().map(diagnosis1 -> diagnosisUtil.mapDiagnosis_ToDiagnosisResponse(diagnosis1)).collect(Collectors.toList()));
        return diagnosis;
    }

    @Override
    public Optional<List<DiagnosisResponse>> fetchDiagnosisByInstitution(PatientRequest patientRequest) {
        return Optional.empty();
    }
}
