package com.astromyllc.shootingstar.clinic.util;

import com.astromyllc.shootingstar.clinic.dto.request.VitalRecordsRequest;
import com.astromyllc.shootingstar.clinic.dto.response.VitalRecordsResponse;
import com.astromyllc.shootingstar.clinic.model.VitalRecords;
import com.astromyllc.shootingstar.clinic.repository.VitalRecordsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VitalRecordsUtil {
    private final VitalRecordsRepository vitalRecordsRepository;

    public static List<VitalRecords> vitalRecordsList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    private void fetAllVitalRecords() {
        vitalRecordsList = vitalRecordsRepository.findAll();
        log.info("Global Vital Records List populated with {} records", vitalRecordsList.size());
    }

    public VitalRecords mapVitalRecordsRequest_ToVitalRecords(VitalRecordsRequest vitalRecordsRequest) {
        return VitalRecords.builder()
                .dateTime(LocalDateTime.parse(vitalRecordsRequest.getDateTime(), formatter))
                .recordType(vitalRecordsRequest.getRecordType())
                .value(vitalRecordsRequest.getValue())
                .institutionCode(vitalRecordsRequest.getInstitutionCode())
                .patientId(vitalRecordsRequest.getPatientId())
                .patientType(vitalRecordsRequest.getPatientType())
                .build();
    }

    public VitalRecordsResponse mapVitalRecords_ToVitalRecordsResponse(VitalRecords vitalRecords) {
        return VitalRecordsResponse.builder()
                .id(vitalRecords.getId())
                .dateTime(vitalRecords.getDateTime())
                .recordType(vitalRecords.getRecordType())
                .value(vitalRecords.getValue())
                .patientId(vitalRecords.getPatientId())
                .patientType(vitalRecords.getPatientType())
                .build();
    }
}
