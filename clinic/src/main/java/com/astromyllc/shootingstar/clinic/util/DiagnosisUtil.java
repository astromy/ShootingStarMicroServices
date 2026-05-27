package com.astromyllc.shootingstar.clinic.util;

import com.astromyllc.shootingstar.clinic.dto.request.DiagnosisRequest;
import com.astromyllc.shootingstar.clinic.dto.response.DiagnosisResponse;
import com.astromyllc.shootingstar.clinic.model.Diagnosis;
import com.astromyllc.shootingstar.clinic.repository.DiagnosisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiagnosisUtil {
    private final DiagnosisRepository diagnosisRepository;
    public static List<Diagnosis> diagnosisGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${gateway.host}")
    private String host;

    @PostConstruct
    private void fetAllDaiagnosis() {
        diagnosisGlobalList = diagnosisRepository.findAll();
        log.info("Global Diagnosis List populated with {} records", diagnosisGlobalList.size());
    }

    public Diagnosis mapDiagnososRequest_ToDiagnosis(DiagnosisRequest diagnosisRequest) {
        return Diagnosis.builder()
                .dateTime(LocalDateTime.parse(diagnosisRequest.getDateTime(),formatter))
                .diagnosis(diagnosisRequest.getDiagnosis())
                .institutionCode(diagnosisRequest.getInstitutionCode())
                .patientId(diagnosisRequest.getPatientId())
                .patientType(diagnosisRequest.getPatientType())
                .build();
    }

    public DiagnosisResponse mapDiagnosis_ToDiagnosisResponse(Diagnosis diagnosis1) {
            return DiagnosisResponse.builder()
                    .id(diagnosis1.getId())
                    .diagnosis(diagnosis1.getDiagnosis())
                    .patientType(diagnosis1.getPatientType())
                    .dateTime(diagnosis1.getDateTime())
                    .patientId(diagnosis1.getPatientId())
                    .institutionCode(diagnosis1.getInstitutionCode())
                .build();
    }
}
